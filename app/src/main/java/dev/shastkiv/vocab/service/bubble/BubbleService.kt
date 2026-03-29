package dev.shastkiv.vocab.service.bubble

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dev.shastkiv.vocab.service.bubble.lifecycle.OverlayLifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Foreground Service responsible for maintaining the bubble's presence in the system.
 * It coordinates the lifecycle of UI managers and handles system events like screen on/off.
 */
@AndroidEntryPoint
class BubbleService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    @Inject lateinit var bubbleManager: BubbleManager
    @Inject lateinit var dialogManager: DialogManager
    @Inject lateinit var serviceNotificationManager: ServiceNotificationManager

    private val overlayLifecycleOwner = OverlayLifecycleOwner()

    companion object {
        private const val TAG = "BubbleService"
        private const val ACTION_STOP_SERVICE = "dev.shastkiv.vocab.STOP_BUBBLE_SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        serviceNotificationManager.startForeground(this)

        overlayLifecycleOwner.create()
        overlayLifecycleOwner.start()
        overlayLifecycleOwner.resume()

        initializeComponents()
        Log.d(TAG, "Foreground Service started with unified notification")
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
        bubbleManager.destroy()
        dialogManager.destroy()
        overlayLifecycleOwner.destroy()
        serviceScope.cancel()
        Log.d(TAG, "User requested service stop: resources cleaned")
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

    private fun handleBubbleClick(bubbleX: Int, bubbleY: Int, bubbleSize: Int) {
        dialogManager.showDialog(
            context = this,
            overlayLifecycleOwner = overlayLifecycleOwner,
            bubbleX = bubbleX,
            bubbleY = bubbleY,
            bubbleSize = bubbleSize
        )
    }

    private fun handleBubbleRemoval() {
        stopSelf()
    }
}
