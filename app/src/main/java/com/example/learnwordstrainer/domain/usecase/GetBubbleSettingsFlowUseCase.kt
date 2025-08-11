package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.model.BubblePosition
import com.example.learnwordstrainer.domain.repository.BubbleSettingsRepository
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetBubbleSettingsFlowUseCase @Inject constructor(
    private val repository: BubbleSettingsRepository
) {
    operator fun invoke() = combine(
        repository.position,
        repository.bubbleSize,
        repository.bubbleTransparency,
        repository.isVibrationEnabled
    ) { position, size, transparency, vibration ->
        BubbleSettings(
            position = position,
            size = size,
            transparency = transparency,
            isVibrationEnabled = vibration
        )
    }

    data class BubbleSettings(
        val position: BubblePosition,
        val size: Float,
        val transparency: Float,
        val isVibrationEnabled: Boolean
    )
}