package com.shastkiv.vocab.service.bubble

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
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
            .setContentTitle(context.getString(R.string.service_notification_title))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(createMainActivityIntent())
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            service.startForeground(
                FOREGROUND_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            service.startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        }
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