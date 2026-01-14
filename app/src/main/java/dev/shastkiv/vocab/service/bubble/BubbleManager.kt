package dev.shastkiv.vocab.service.bubble

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.domain.usecase.GetBubbleSettingsFlowUseCase
import dev.shastkiv.vocab.domain.usecase.SaveBubblePositionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BubbleManager - manages bubble functionality using UseCase pattern.
 * Handles: positioning, display, drag&drop, settings updates.
 *
 * Uses UseCase pattern because bubble operations are simple CRUD operations
 * that don't require complex UI state management.
 */
@Singleton
class BubbleManager @Inject constructor(
    private val getBubbleSettingsFlowUseCase: GetBubbleSettingsFlowUseCase,
    private val saveBubblePositionUseCase: SaveBubblePositionUseCase
) {

    private var bubbleViewManager: BubbleViewManager? = null
    private var isInitialized = false

    suspend fun initialize(
        context: Context,
        coroutineScope: CoroutineScope,
        onBubbleClick: () -> Unit,
        onBubbleRemoval: () -> Unit
    ) {
        if (isInitialized) {
            Log.w(TAG, "BubbleManager already initialized")
            return
        }

        try {
            val initialSettings = getBubbleSettingsFlowUseCase().first()

            bubbleViewManager = BubbleViewManager(
                context = context,
                coroutineScope = coroutineScope,
                saveBubblePositionUseCase = saveBubblePositionUseCase,
                onBubbleClick = onBubbleClick,
                onBubbleRemovedByUser = onBubbleRemoval,
                initialSize = initialSettings.size.dp,
                initialAlpha = initialSettings.transparency / 100f
            )

            bubbleViewManager?.show(initialSettings.position)
            isInitialized = true

            Log.d(TAG, "Bubble UI initialized: SPECIAL_USE active")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize bubble", e)
            throw e
        }
    }

    fun observeSettings(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            getBubbleSettingsFlowUseCase().collect { settings ->
                updateBubbleSettings(
                    newSize = settings.size.dp,
                    newAlpha = settings.transparency / 100f,
                    isVibrationEnabled = settings.isVibrationEnabled
                )
            }
        }
    }

    fun hideViews() {
        bubbleViewManager?.hideViews()
    }

    fun showViews() {
        bubbleViewManager?.showViews()
    }

    private fun updateBubbleSettings(
        newSize: Dp,
        newAlpha: Float,
        isVibrationEnabled: Boolean
    ) {
        bubbleViewManager?.updateBubbleSettings(
            newSize = newSize,
            newAlpha = newAlpha,
            isVibrationEnabled = isVibrationEnabled
        )
        Log.d(TAG, "Bubble settings updated: size=$newSize, alpha=$newAlpha")
    }

    fun destroy() {
        bubbleViewManager?.destroy()
        bubbleViewManager = null
        isInitialized = false
        Log.d(TAG, "STOPPING SERVICE: BubbleManager destroyed, all overlays removed")
    }

    companion object {
        private const val TAG = "BubbleManager"
    }
}