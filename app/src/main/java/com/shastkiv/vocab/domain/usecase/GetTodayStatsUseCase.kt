package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.repository.DailyStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayStatsUseCase @Inject constructor(
    private val statsRepository: DailyStatsRepository
) {

    operator fun invoke(): Flow<DailyStatistic?> {
        return statsRepository.getTodayStatistic()
    }
}