package com.example.music_app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings.Global.getString
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.media.session.MediaButtonReceiver
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.media.app.NotificationCompat as MediaNotificationCompat // Để tránh xung đột với package NotificationCompat


class NotificationMediaPlayer @Inject constructor(@ApplicationContext val context: Context) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "MusicPlayerChannel"
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_RESUME = "RESUME"
    }

    @SuppressLint("SuspiciousIndentation")
    fun sendNotification(title: String, author: String, icPause: Int): Notification {

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        val pauseIntent = Intent(context, NotificationReceiver::class.java).setAction(ACTION_PAUSE)
        val pausePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val resumeIntent =
            Intent(context, NotificationReceiver::class.java).setAction(ACTION_RESUME)
        val resumePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            resumeIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mediaSession = MediaSessionCompat(context, "tag")
        val bigPicture = BitmapFactory.decodeResource(context.resources, R.drawable.back_music)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // Show controls on lock screen even when user hides sensitive content.
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.play)
//            .addAction(R.drawable.previous, "Previous", null) // #0
            .addAction(icPause, "PAUSE", pausePendingIntent) // #1
//            .addAction(R.drawable.next, "Next", null) // #2
            .setColor(Color.BLUE)
            .setStyle(
                MediaNotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1 /* #1: pause button \*/)
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle(title)
            .setContentText(author)
            .setLargeIcon(bigPicture)
            .setOnlyAlertOnce(true)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
        return notification.build()
    }

}