package com.example.learnwordstrainer.domain

import com.example.learnwordstrainer.repository.BubbleSettingsRepository

class SaveBubblePositionUseCase(private val repository: BubbleSettingsRepository) {
    suspend operator fun invoke(x: Int, y: Int) = repository.savePosition(x, y)
}