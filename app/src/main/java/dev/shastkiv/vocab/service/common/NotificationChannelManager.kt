package dev.shastkiv.vocab.service.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dev.shastkiv.vocab.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_SERVICE = "vocab_service_notification"
        const val CHANNEL_POPUP = "vocab_popup_notification"
    }

    fun createNotificationChannels() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val foregroundChannel = NotificationChannel(
            CHANNEL_SERVICE,
            context.getString(R.string.service_channel_title),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.service_channel_description)
            enableVibration(false)
            setSound(null, null)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PRIVATE
        }

        val popupChannel = NotificationChannel(
            CHANNEL_POPUP,
            context.getString(R.string.popup_channel_title),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.popup_channel_description)
            enableVibration(false)
            setVibrationPattern(longArrayOf(0))
            enableLights(false)
            setSound(null, null)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            setBypassDnd(false)
        }

        notificationManager.createNotificationChannel(foregroundChannel)
        notificationManager.createNotificationChannel(popupChannel)
    }

    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}
