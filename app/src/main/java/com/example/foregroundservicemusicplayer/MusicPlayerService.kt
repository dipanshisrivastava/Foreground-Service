package com.example.foregroundservicemusicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicPlayerService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "MusicPlayerChannel"
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        mediaPlayer.setOnCompletionListener {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audioUri = Uri.parse("android.resource://${packageName}/${R.raw.my_music}") // Replace "your_audio_file" with the name of your audio file in the "res/raw" folder
        mediaPlayer.apply {
            reset()
            setDataSource(applicationContext, audioUri)
            prepare()
            start()
        }
        showNotification()
        isPlaying = true
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        isPlaying = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Specify the mutability flag
        )

        // Use a large icon (if available) for the notification
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_music_note_large)

        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Now Playing: Your Audio File")
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(largeIcon)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                R.drawable.ic_pause,
                "Pause",
                createPendingIntent(MusicAction.PAUSE)
            )
            .addAction(
                R.drawable.ic_stop,
                "Stop",
                createPendingIntent(MusicAction.STOP)
            )
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(action: MusicAction): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java)
        intent.action = action.name
        return PendingIntent.getService(
            this,
            action.ordinal,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    enum class MusicAction {
        PAUSE,
        STOP
    }
}
