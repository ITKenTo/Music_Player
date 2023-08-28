package com.example.music_app.Service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.widget.SeekBar
import com.example.music_app.ChillMediaPlayer
import com.example.music_app.MainActivity
import com.example.music_app.NotificationMediaPlayer
import com.example.music_app.NotificationMediaPlayer.Companion.ACTION_PAUSE
import com.example.music_app.NotificationMediaPlayer.Companion.NOTIFICATION_ID
import com.example.music_app.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChillMediaPlayerService : Service() {
    private var binder = LocalBinder()

    @Inject
    lateinit var chillMediaPlayer: ChillMediaPlayer

    @Inject
    lateinit var notificationMediaPlayer: NotificationMediaPlayer

    companion object {
        var title_notification: String? = null
        var author_notification: String? = null
    }

    inner class LocalBinder : Binder() {
        fun getService(): ChillMediaPlayerService = this@ChillMediaPlayerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE ->
                if (chillMediaPlayer.isPlaying()) {
                    pause()
                    val notification =
                        title_notification?.let {
                            author_notification?.let { it1 ->
                                notificationMediaPlayer.sendNotification(
                                    it,
                                    it1, R.drawable.play
                                )
                            }
                        }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForeground(NOTIFICATION_ID, notification)
                    }
                    MainActivity.binding.btnPlay.setImageResource(R.drawable.playbutton)
                    MainActivity.check = true
                } else {
                    resume()
                    val notification =
                        title_notification?.let {
                            author_notification?.let { it1 ->
                                notificationMediaPlayer.sendNotification(
                                    it,
                                    it1, R.drawable.pause
                                )
                            }
                        }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForeground(NOTIFICATION_ID, notification)
                    }
                    MainActivity.binding.btnPlay.setImageResource(R.drawable.pause_1)
                    MainActivity.check = false
                }
        }

        return START_STICKY
    }

    fun pauseNotification() {
        val notification =
            title_notification?.let {
                author_notification?.let { it1 ->
                    notificationMediaPlayer.sendNotification(
                        it,
                        it1, R.drawable.play
                    )
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification)
        }
        MainActivity.binding.btnPlay.setImageResource(R.drawable.playbutton)
        MainActivity.check = true
    }

    fun resumeNotification() {
        val notification =
            title_notification?.let {
                author_notification?.let { it1 ->
                    notificationMediaPlayer.sendNotification(
                        it,
                        it1, R.drawable.pause
                    )
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification)
        }
        MainActivity.binding.btnPlay.setImageResource(R.drawable.pause_1)
        MainActivity.check = false
    }

    fun setSource(path: String, title: String, author: String) {
        title_notification = title
        author_notification = author
        chillMediaPlayer.setSource(path)
        chillMediaPlayer.playOrResume()
//        notificationMediaPlayer.sendNotification(title,author,R.drawable.pause)
        val notification = notificationMediaPlayer.sendNotification(title, author, R.drawable.pause)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    fun updateMusic() {
        MainActivity.binding.linerController.visibility = View.VISIBLE
        MainActivity.binding.tvNameMusic.text = title_notification

        if (chillMediaPlayer.isPlaying()) {
            MainActivity.check = false
            MainActivity.binding.btnPlay.setImageResource(R.drawable.pause_1)
        } else {
            MainActivity.check = true
            MainActivity.binding.btnPlay.setImageResource(R.drawable.playbutton)
        }
        seekbar(MainActivity.binding.seekbar)
    }

    fun pause() {
        chillMediaPlayer.pause()
    }

    fun resume() {
        chillMediaPlayer.playOrResume()
    }

    fun seekbar(seekBar: SeekBar) {
        chillMediaPlayer.seekbar(seekBar)
    }

    override fun onBind(intent: Intent): IBinder = binder

}