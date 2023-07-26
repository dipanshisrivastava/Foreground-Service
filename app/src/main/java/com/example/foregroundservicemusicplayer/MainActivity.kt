package com.example.foregroundservicemusicplayer

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.foregroundservicemusicplayer.databinding.ActivityMainBinding // Replace "yourapp" with your actual package name

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        val button = findViewById<Button>(R.id.btn_start)
        setContentView(view)

        binding.btnStart.setOnClickListener {
            Log.d("MusicPlayerService", " Before check")
            startMusicPlayerService()
        }

        binding.btnStop.setOnClickListener {
            stopMusicPlayerService()
        }
    }

    private fun startMusicPlayerService() {
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun stopMusicPlayerService() {
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        stopService(serviceIntent)
    }
}