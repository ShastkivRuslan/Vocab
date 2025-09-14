package com.shastkiv.vocab.service

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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.addwordfloating.AddWordFloatingViewModel
import com.shastkiv.vocab.ui.lifecycle.OverlayLifecycleOwner
import com.shastkiv.vocab.ui.mainscreen.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * BubbleService - Service coordinator implementing clean architecture separation.
 *
 * ARCHITECTURE DECISIONS:
 *
 * 1. BubbleManager uses UseCase pattern because:
 *    - Simple CRUD operations (save position, get settings)
 *    - No complex UI state management needed
 *    - Direct data operations without sophisticated coordination
 *
 * 2. DialogManager uses ViewModel Factory pattern because:
 *    - Complex UI state machine (Loading â†’ Success â†’ SavingWord â†’ etc.)
 *    - Sophisticated animation states and transitions
 *    - Coordination of multiple UseCase for single UI flow
 *    - Reactive StateFlow management for Compose
 *    - Lifecycle-aware operations with cleanup
 *
 * This hybrid approach is architecturally sound:
 * - UseCase for simple operations (CRUD)
 * - ViewModel for complex UI coordination
 *
 * BubbleService acts as mediator, coordinating between separated concerns
 * without mixing responsibilities within single classes.
 */
@AndroidEntryPoint
class BubbleService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isBubbleVisible = false

    // Clean separation: different patterns for different complexity levels
    @Inject lateinit var bubbleManager: BubbleManager
    @Inject lateinit var dialogManager: DialogManager

    // Shared overlay lifecycle for both managers
    private val overlayLifecycleOwner = OverlayLifecycleOwner()

    private val screenStateReceiver = ScreenStateReceiver()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BubbleService onCreate")

        isBubbleVisible = true
        startForegroundService()
        registerScreenStateReceiver()
        initializeComponents()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                Log.d(TAG, "Stop service requested")
                stopSelf()
                START_NOT_STICKY
            }
            else -> START_STICKY
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "BubbleService onDestroy")
        cleanupResources()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun initializeComponents() {
        serviceScope.launch {
            try {
                bubbleManager.initialize(
                    context = this@BubbleService,
                    coroutineScope = serviceScope,
                    onBubbleClick = ::handleBubbleClick,
                    onBubbleRemoval = ::handleBubbleRemoval
                )

                bubbleManager.observeSettings(serviceScope)

                Log.d(TAG, "All components initialized successfully")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize components", e)
                stopSelf()
            }
        }
    }

    /**
     * Handle bubble click - coordinate between managers
     */
    private fun handleBubbleClick() {
        Log.d("BubbleService", "handleBubbleClick called")
        dialogManager.showDialog(
            context = this,
            overlayLifecycleOwner = overlayLifecycleOwner
        )
    }

    private fun handleBubbleRemoval() {
        Log.d(TAG, "Bubble removed by user")
        stopSelf()
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun registerScreenStateReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenStateReceiver, filter)
    }

    private fun updateNotificationState() {
        val notification = createNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        createNotificationChannelIfNeeded()

        val mainIntent = createMainActivityIntent()
        val stopIntent = createStopServiceIntent()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(getNotificationText())
            .setSmallIcon(R.drawable.ic_bubble)
            .setContentIntent(mainIntent)
            .addAction(R.drawable.ic_close, getString(R.string.stop), stopIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannelIfNeeded() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.bubble_service_channel_name),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = getString(R.string.bubble_service_channel_description)
        }

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun createMainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this,
            REQUEST_CODE_MAIN_ACTIVITY,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createStopServiceIntent(): PendingIntent {
        val intent = Intent(this, BubbleService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        return PendingIntent.getService(
            this,
            REQUEST_CODE_STOP_SERVICE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getNotificationText(): String {
        return if (isBubbleVisible) {
            getString(R.string.bubble_active)
        } else {
            getString(R.string.bubble_paused)
        }
    }

    private fun cleanupResources() {
        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "ScreenStateReceiver was not registered", e)
        }

        bubbleManager.destroy()
        dialogManager.destroy()
        overlayLifecycleOwner.destroy()

        serviceScope.cancel()
    }

    private inner class ScreenStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> handleScreenOff()
                Intent.ACTION_USER_PRESENT -> handleScreenOn()
            }
        }

        private fun handleScreenOff() {
            Log.d(TAG, "Screen turned off")
            isBubbleVisible = false
            bubbleManager.hideViews()
            updateNotificationState()
        }

        private fun handleScreenOn() {
            Log.d(TAG, "Screen unlocked")
            isBubbleVisible = true
            bubbleManager.showViews()
            updateNotificationState()
        }
    }

    companion object {
        private const val TAG = "BubbleService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "bubble_service_channel"
        private const val REQUEST_CODE_MAIN_ACTIVITY = 100
        private const val REQUEST_CODE_STOP_SERVICE = 101

        const val ACTION_STOP_SERVICE = "com.example.learnwordstrainer.STOP_BUBBLE_SERVICE"

        fun createStartIntent(context: Context): Intent {
            return Intent(context, BubbleService::class.java)
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, BubbleService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
        }
    }
}