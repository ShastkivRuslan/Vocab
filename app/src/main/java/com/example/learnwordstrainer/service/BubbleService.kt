package com.example.learnwordstrainer.service

import BubbleSettingsRepository
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.domain.GetBubbleSettingsFlowUseCase
import com.example.learnwordstrainer.domain.SaveBubblePositionUseCase
import com.example.learnwordstrainer.ui.activities.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class BubbleService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var bubbleViewManager: BubbleViewManager? = null
    private var isBubbleVisible = false

    private val settingsRepository by lazy { BubbleSettingsRepository(this) }
    private val getBubbleSettingsFlowUseCase by lazy { GetBubbleSettingsFlowUseCase(settingsRepository) }
    private val saveBubblePositionUseCase by lazy { SaveBubblePositionUseCase(settingsRepository) }

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    isBubbleVisible = false
                    bubbleViewManager?.hideViews()
                }
                Intent.ACTION_USER_PRESENT -> {
                    isBubbleVisible = true
                    bubbleViewManager?.showViews()
                }
            }
            updateNotification()
        }
    }

    override fun onCreate() {
        super.onCreate()
        isBubbleVisible = true

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        registerReceiver(screenStateReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        })

        initBubble()
        observeSettings()
    }

    private fun initBubble() {
        if (bubbleViewManager != null) return

        serviceScope.launch {
            val initialSettings = getBubbleSettingsFlowUseCase().first()

            bubbleViewManager = BubbleViewManager(
                context = this@BubbleService,
                coroutineScope = serviceScope,
                saveBubblePositionUseCase = saveBubblePositionUseCase,
                onBubbleRemovedByUser = { stopSelf() }
            ).apply {
                show(initialSettings.position)
            }
        }
    }

    private fun observeSettings() {
        // Тут можна додати слухачі налаштувань, якщо потрібно
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        bubbleViewManager?.destroy()
        bubbleViewManager = null
        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: IllegalArgumentException) {
            // Ігноруємо, якщо приймач вже був відреєстрований
        }
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val channelId = "bubble_service_channel"
        val channel = NotificationChannel(channelId, "Bubble Service", NotificationManager.IMPORTANCE_MIN)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this, BubbleService::class.java).apply { action = ACTION_STOP_SERVICE }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Vocab.")
            .setContentText(if (isBubbleVisible) "Бульбашка активна" else "Бульбашка призупинена")
            .setSmallIcon(R.drawable.ic_bubble)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_close, "Зупинити", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_SERVICE = "com.example.learnwordstrainer.STOP_BUBBLE_SERVICE"
    }
}