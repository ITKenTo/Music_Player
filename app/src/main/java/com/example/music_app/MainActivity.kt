package com.example.music_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_app.Service.ChillMediaPlayerService
import com.example.music_app.adapter.MusicAdapter
import com.example.music_app.databinding.ActivityMainBinding
import com.example.music_app.model.MusicFileModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var listMusic: ArrayList<MusicFileModel>
    private var mBound: Boolean = false
    private lateinit var chillMediaPlayerService: ChillMediaPlayerService

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityMainBinding
        var check: Boolean = true
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ChillMediaPlayerService.LocalBinder
            chillMediaPlayerService = binder.getService()
            mBound = true
            chillMediaPlayerService.updateMusic()
            binding.btnPlay.setOnClickListener {
                if (check) {
                    binding.btnPlay.setImageResource(R.drawable.pause_1)
                    chillMediaPlayerService.resume()
                    chillMediaPlayerService.resumeNotification()
                    check = false
                } else {
                    binding.btnPlay.setImageResource(R.drawable.playbutton)
                    chillMediaPlayerService.pause()
                    chillMediaPlayerService.pauseNotification()
                    check = true
                }
            }
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listMusic = ArrayList()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!checkPermission()) {
            requestPermission()
            return
        }
        binding.recyclerMusic.layoutManager = LinearLayoutManager(this)
        readMusic()
        initView()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, ChillMediaPlayerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    private fun initView() {

        val adapter = MusicAdapter(listMusic, onClick = {
            if (mBound) {
                chillMediaPlayerService.setSource(it.path, it.title, it.artist)
                binding.linerController.visibility = View.VISIBLE
                binding.btnPlay.setImageResource(R.drawable.pause_1)
                check = false
                binding.tvNameMusic.text = it.title
                //gắn dữ liệu để hiển thị seekbar
                chillMediaPlayerService.seekbar(binding.seekbar)
            }
        })
        binding.recyclerMusic.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun readMusic() {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder
        )
        cursor?.use {
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationsColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)
                val path = it.getString(dataColumn)
                val artist = it.getString(artistColumn)
                val duration = it.getString(durationsColumn)
                listMusic.add(MusicFileModel(path, title, artist, duration))
            }
        }

        Log.d("TAG", "readMusic: $listMusic")
    }

    private fun checkPermission(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity, Manifest.permission.READ_MEDIA_AUDIO
            )
        ) {
            Toast.makeText(this, "READ PERMISSION IS REA", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this@MainActivity, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), 999
                )
            }
        }
    }
}