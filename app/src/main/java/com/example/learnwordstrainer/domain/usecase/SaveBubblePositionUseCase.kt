package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.data.repository.BubbleSettingsRepository

class SaveBubblePositionUseCase(private val repository: BubbleSettingsRepository) {
    suspend operator fun invoke(x: Int, y: Int) = repository.savePosition(x, y)
}