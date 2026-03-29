package dev.shastkiv.vocab.service.bubble

import android.content.Context
import android.util.Log
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.domain.model.BubblePosition
import dev.shastkiv.vocab.domain.usecase.GetBubbleSettingsFlowUseCase
import dev.shastkiv.vocab.domain.usecase.SaveBubblePositionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

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
        // ← Сигнатура оновлена: передаємо координати і розмір бульбашки
        onBubbleClick: (bubbleX: Int, bubbleY: Int, bubbleSize: Int) -> Unit,
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

            Log.d(TAG, "Bubble UI initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize bubble", e)
            throw e
        }
    }

    fun observeSettings(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            getBubbleSettingsFlowUseCase().collect { settings ->
                bubbleViewManager?.updateBubbleSettings(
                    newSize = settings.size.dp,
                    newAlpha = settings.transparency / 100f,
                    isVibrationEnabled = settings.isVibrationEnabled
                )
            }
        }
    }

    fun destroy() {
        bubbleViewManager?.destroy()
        bubbleViewManager = null
        isInitialized = false
        Log.d(TAG, "BubbleManager destroyed")
    }

    companion object {
        private const val TAG = "BubbleManager"
    }
}
