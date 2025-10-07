package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.StatType
import com.shastkiv.vocab.domain.repository.DailyStatsRepository
import javax.inject.Inject

class UpdateDailyStatsUseCase @Inject constructor(
    private val statsRepository: DailyStatsRepository
) {
    suspend operator fun invoke(type: StatType) {
        statsRepository.updateStatistic(type)
    }
}