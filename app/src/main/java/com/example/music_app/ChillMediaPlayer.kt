package com.example.music_app

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.SeekBar
import java.util.logging.Handler
import javax.inject.Inject


class ChillMediaPlayer @Inject constructor(private var mediaPlayer: MediaPlayer) {

     fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun setSource(path: String) {
        var currentMusicPath: String? = null
        if (path.isNotEmpty() && path != currentMusicPath) {
            currentMusicPath = path
            mediaPlayer.apply {
                release()
            }
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(path)
                prepare()
                start()
            }
        }
    }

    fun playOrResume() {
        return mediaPlayer.start()
    }

    fun pause() {
        if (isPlaying()) {
            return mediaPlayer.pause()
        }
    }

    fun stop() {
        if (isPlaying()) {
            mediaPlayer.release()
            mediaPlayer.stop()
        }
    }

    fun seekbar(seekBar: SeekBar) {
        val currentPosition = mediaPlayer.currentPosition
        val totalDuration = mediaPlayer.duration

        seekBar.max = totalDuration
        seekBar.progress = currentPosition.toInt()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val handler = android.os.Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    seekBar.progress = 0
                }
            }

        }, 0)

    }
}