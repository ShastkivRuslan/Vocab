package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.enums.StatType
import com.shastkiv.vocab.domain.repository.StatisticRepository
import javax.inject.Inject

class UpdateDailyStatsUseCase @Inject constructor(
    private val statsRepository: StatisticRepository
) {
    suspend operator fun invoke(type: StatType) {
        statsRepository.updateStatistic(type)
    }
}