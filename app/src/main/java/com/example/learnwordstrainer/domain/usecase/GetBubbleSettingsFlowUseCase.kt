package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.model.BubblePosition
import com.example.learnwordstrainer.data.repository.BubbleSettingsRepository
import kotlinx.coroutines.flow.combine

class GetBubbleSettingsFlowUseCase(private val repository: BubbleSettingsRepository) {
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