package com.example.learnwordstrainer.domain

import BubbleSettingsRepository

class SaveBubblePositionUseCase(private val repository: BubbleSettingsRepository) {
    suspend operator fun invoke(x: Int, y: Int) = repository.savePosition(x, y)
}