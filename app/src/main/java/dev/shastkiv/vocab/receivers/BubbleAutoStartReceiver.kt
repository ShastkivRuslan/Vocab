package dev.shastkiv.vocab.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import dev.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import dev.shastkiv.vocab.service.bubble.BubbleService
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
        private const val BOOT_DELAY_MS = 10000L
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
        if (!Settings.canDrawOverlays(context)) {
            Log.e(TAG, "Cannot start service: Overlay permission not granted.")
            return
        }

        val isEnabled = bubbleSettingsRepository.isBubbleEnabled.first()
        if (!isEnabled) {
            Log.d(TAG, "Bubble service is disabled in settings. Aborting.")
            return
        }

        val isBootAction = reason == Intent.ACTION_BOOT_COMPLETED ||
                reason == Intent.ACTION_USER_UNLOCKED
        if (isBootAction) {
            Log.d(TAG, "Boot action detected, waiting ${BOOT_DELAY_MS}ms before start")
            delay(BOOT_DELAY_MS)
        }

        try {
            context.startForegroundService(Intent(context, BubbleService::class.java))
            Log.i(TAG, "Bubble service started for action: $reason")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start BubbleService: ${e.message}", e)
        }
    }
}