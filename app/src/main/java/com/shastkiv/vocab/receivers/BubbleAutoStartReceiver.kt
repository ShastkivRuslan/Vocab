package com.shastkiv.vocab.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import com.shastkiv.vocab.service.bubble.BubbleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BubbleAutoStartReceiver : BroadcastReceiver() {
    @Inject
    lateinit var bubbleSettingsRepository: BubbleSettingsRepository

    companion object {
        private const val TAG = "BubbleAutoStartReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(TAG, "Received action: $action")

        val validActions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED,
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_USER_UNLOCKED
        )

        if (action !in validActions) return

        val pendingResult = goAsync()
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            try {
                startBubbleServiceIfAllowed(context, action)
            } finally {
                pendingResult.finish()
            }
        }
    }

    /**
     * Initiates the bubble service if all conditions are met.
     * Implements a double-check pattern with a safety delay to prevent
     * starting the service if the user disabled it during the system boot period.
     */
    private suspend fun startBubbleServiceIfAllowed(context: Context, reason: String) {
        if (!Settings.canDrawOverlays(context)) {
            Log.e(TAG, "Cannot start service: Overlay permission not granted.")
            return
        }

        val isEnabledInitial = bubbleSettingsRepository.isBubbleEnabled.first()
        if (!isEnabledInitial) {
            Log.d(TAG, "Initial check: Bubble service is disabled in settings. Aborting.")
            return
        }

        Log.d(TAG, "Starting 10s safety delay before start for safety start on boot for action: $reason")

        delay(10000)

        try {
            val serviceIntent = Intent(context, BubbleService::class.java)
            context.startForegroundService(serviceIntent)
            Log.i(TAG, "Bubble service successfully started after safety delay.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start BubbleService: ${e.message}", e)
        }
    }
}