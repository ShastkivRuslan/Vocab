package com.example.learnwordstrainer.domain

import BubbleSettingsRepository
import kotlinx.coroutines.flow.combine

class GetBubbleSettingsFlowUseCase(private val repository: BubbleSettingsRepository) {
    operator fun invoke() = combine(
        repository.position,
        repository.bubbleSize,
        repository.bubbleTransparency
    ) { position, size, transparency ->
        BubbleSettings(
            position = position,
            size = size,
            transparency = transparency
        )
    }

    data class BubbleSettings(
        val position: com.example.learnwordstrainer.model.BubblePosition,
        val size: Float,
        val transparency: Float
    )
}