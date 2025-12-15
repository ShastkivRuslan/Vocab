package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.repository.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayStatsUseCase @Inject constructor(
    private val statsRepository: StatisticRepository
) {

    operator fun invoke(): Flow<DailyStatistic?> {
        return statsRepository.getTodayStatistic()
    }
}