package com.shastkiv.vocab.service.bubble

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.shastkiv.vocab.R
import com.shastkiv.vocab.service.common.NotificationChannelManager
import com.shastkiv.vocab.ui.mainscreen.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    channelManager: NotificationChannelManager
) {

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1001
    }

    init {
        channelManager.createNotificationChannels()
    }

    fun startForeground(service: Service) {
        val notification = NotificationCompat.Builder(context,
            NotificationChannelManager.CHANNEL_SERVICE
        )
            .setSmallIcon(R.drawable.ic_add_floating)
            .setContentTitle("Vocab+ working...")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(createMainActivityIntent())
            .build()

        service.startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun createMainActivityIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}