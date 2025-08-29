package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.repository.DailyStatsRepository
import javax.inject.Inject

enum class StatType {
    WORD_ASKED,
    WORD_ADDED,
    CORRECT_ANSWER,
    WRONG_ANSWER
}

class UpdateDailyStatsUseCase @Inject constructor(
    private val statsRepository: DailyStatsRepository
) {
    suspend operator fun invoke(type: StatType) {
        statsRepository.updateStatistic(type)
    }
}