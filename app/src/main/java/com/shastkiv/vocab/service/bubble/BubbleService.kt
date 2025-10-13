package com.shastkiv.vocab.service.bubble

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.shastkiv.vocab.ui.lifecycle.OverlayLifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class BubbleService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isBubbleVisible = false

    @Inject lateinit var bubbleManager: BubbleManager
    @Inject lateinit var dialogManager: DialogManager
    @Inject lateinit var serviceNotificationManager: ServiceNotificationManager

    private val overlayLifecycleOwner = OverlayLifecycleOwner()
    private val screenStateReceiver = ScreenStateReceiver()

    companion object {
        private const val TAG = "BubbleService"
        private const val ACTION_STOP_SERVICE = "com.shastkiv.vocab.STOP_BUBBLE_SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BubbleService onCreate")

        serviceNotificationManager.startForeground(this)

        isBubbleVisible = true
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

    private fun handleBubbleClick() {
        Log.d(TAG, "handleBubbleClick called")
        dialogManager.showDialog(
            context = this,
            overlayLifecycleOwner = overlayLifecycleOwner
        )
    }

    private fun handleBubbleRemoval() {
        Log.d(TAG, "Bubble removed by user")
        stopSelf()
    }

    private fun registerScreenStateReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenStateReceiver, filter)
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
        }

        private fun handleScreenOn() {
            Log.d(TAG, "Screen unlocked")
            isBubbleVisible = true
            bubbleManager.showViews()
        }
    }
}