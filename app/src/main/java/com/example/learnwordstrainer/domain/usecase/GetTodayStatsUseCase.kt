package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.model.DailyStatistic
import com.example.learnwordstrainer.domain.repository.DailyStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayStatsUseCase @Inject constructor(
    private val statsRepository: DailyStatsRepository
) {

    operator fun invoke(): Flow<DailyStatistic?> {
        return statsRepository.getTodayStatistic()
    }
}