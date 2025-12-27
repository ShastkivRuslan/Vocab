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

    private suspend fun startBubbleServiceIfAllowed(context: Context, reason: String) {
        if (!bubbleSettingsRepository.isBubbleEnabled.first()) {
            Log.d(TAG, "Bubble service is disabled in settings. Aborting.")
            return
        }

        if (!Settings.canDrawOverlays(context)) {
            Log.w(TAG, "Cannot start bubble service: overlay permission not granted")
            return
        }

        try {
            Log.d(TAG, "Starting 10s safety delay for action: $reason")
            kotlinx.coroutines.delay(10000)

            val serviceIntent = Intent(context, BubbleService::class.java)

            context.startForegroundService(serviceIntent)
            Log.i(TAG, "Successfully started bubble service after 10s delay. Reason: $reason")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service after delay: ${e.message}")
        }
    }
}