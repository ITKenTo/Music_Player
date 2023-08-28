package com.example.music_app.di

import android.content.Context
import android.media.MediaPlayer
import com.example.music_app.ChillMediaPlayer
import com.example.music_app.MainActivity
import com.example.music_app.NotificationMediaPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaPlayerModule {

    @Singleton
    @Provides
    fun provideMediaPlayer():MediaPlayer{
        return MediaPlayer()
    }

    @Singleton
    @Provides
    fun provideChillMediaPlayer(player: MediaPlayer):ChillMediaPlayer{
        return ChillMediaPlayer(player)
    }

    @Singleton
    @Provides
    fun provideNotification(@ApplicationContext appContext: Context):NotificationMediaPlayer{
        return NotificationMediaPlayer(appContext)
    }
}