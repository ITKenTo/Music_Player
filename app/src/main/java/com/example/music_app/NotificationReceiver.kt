package com.example.music_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.music_app.NotificationMediaPlayer.Companion.ACTION_PAUSE
import com.example.music_app.NotificationMediaPlayer.Companion.ACTION_RESUME
import com.example.music_app.Service.ChillMediaPlayerService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var chillMediaPlayer: ChillMediaPlayer

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_PAUSE -> {
                val pauseIntent = Intent(context, ChillMediaPlayerService::class.java)
                pauseIntent.action = ACTION_PAUSE
                ContextCompat.startForegroundService(context!!, pauseIntent)
            }
        }
    }
}