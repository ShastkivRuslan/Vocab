package dev.shastkiv.vocab.service.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.notification.NotificationState
import dev.shastkiv.vocab.service.common.NotificationChannelManager
import dev.shastkiv.vocab.ui.mainscreen.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val channelManager: NotificationChannelManager
) {

    companion object {
        const val NOTIFICATION_ID_POPUP = 1002
        private const val AUTO_DISMISS_DELAY = 5000L
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val handler = Handler(Looper.getMainLooper())

    private var currentState: NotificationState? = null
    private var autoDismissRunnable: Runnable? = null

    init {
        channelManager.createNotificationChannels()
    }

    fun updateState(newState: NotificationState) {
        autoDismissRunnable?.let { handler.removeCallbacks(it) }

        currentState = newState
        showPopupNotification(buildNotification(newState))

        autoDismissRunnable = Runnable {
            dismissPopupNotification()
            currentState = null
        }
        handler.postDelayed(autoDismissRunnable!!, AUTO_DISMISS_DELAY)
    }

    fun updateToGentleNudgeState(wordCount: Int) = updateState(NotificationState.GentleNudge(wordCount))
    fun updateToWordEchoState(word: Word, daysAgo: Int) = updateState(NotificationState.WordEcho(word, daysAgo))
    fun updateToStreakKeeperState(days: Int) = updateState(NotificationState.StreakKeeper(days))
    fun updateToSuccessMomentState(word: String) = updateState(NotificationState.SuccessMoment(word))

    private fun buildNotification(state: NotificationState): android.app.Notification {
        return when (state) {
            is NotificationState.WordEcho -> buildWordEchoNotification(state.word, state.daysAgo)
            is NotificationState.GentleNudge -> buildGentleNudgeNotification(state.wordCount)
            is NotificationState.StreakKeeper -> buildStreakKeeperNotification(state.streakDays)
            is NotificationState.SuccessMoment -> buildSuccessMomentNotification(state.word)
        }
    }

    private fun showPopupNotification(notification: android.app.Notification) {
        notificationManager.notify(NOTIFICATION_ID_POPUP, notification)
        Log.d("NotificationStateManager", "Showing popup notification")
    }

    private fun dismissPopupNotification() {
        notificationManager.cancel(NOTIFICATION_ID_POPUP)
        Log.d("NotificationStateManager", "Dismissed popup notification")
    }

    private fun isUserActive(): Boolean {
        return powerManager.isInteractive
    }

    private fun buildGentleNudgeNotification(wordCount: Int): android.app.Notification {
        return NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_POPUP)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("📚 Час повторити слова")
            .setContentText("У вас $wordCount слів для повторення")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setSound(null)
            .setVibrate(longArrayOf(0))
            .setOnlyAlertOnce(true)
            .setDefaults(0)
            .setContentIntent(createMainActivityIntent())
            .build()
    }

    private fun buildWordEchoNotification(word: Word, daysAgo: Int): android.app.Notification {
        return NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_POPUP)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🫧 ${word.sourceWord}")
            .setContentText(word.translation)
            .setSubText("Додано ${daysAgo}д тому")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setSound(null)
            .setVibrate(longArrayOf(0))
            .setOnlyAlertOnce(true)
            .setDefaults(0)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(word.translation)
            )
            .setContentIntent(createMainActivityIntent())
            .build()
    }

    private fun buildStreakKeeperNotification(streakDays: Int): android.app.Notification {
        return NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_POPUP)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔥 Streak: $streakDays днів")
            .setContentText("Не втрачай свою серію!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setSound(null)
            .setVibrate(longArrayOf(0))
            .setOnlyAlertOnce(true)
            .setDefaults(0)
            .setContentIntent(createMainActivityIntent())
            .build()
    }

    private fun buildSuccessMomentNotification(word: String): android.app.Notification {
        return NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_POPUP)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("✨ Чудова робота!")
            .setContentText("Ви вивчили слово: $word")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setSound(null)
            .setVibrate(longArrayOf(0))
            .setOnlyAlertOnce(true)
            .setDefaults(0)
            .setContentIntent(createMainActivityIntent())
            .build()
    }

    private fun createMainActivityIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}