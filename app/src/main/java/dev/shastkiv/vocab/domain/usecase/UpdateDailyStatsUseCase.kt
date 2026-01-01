package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.enums.StatType
import dev.shastkiv.vocab.domain.repository.StatisticRepository
import javax.inject.Inject

class UpdateDailyStatsUseCase @Inject constructor(
    private val statsRepository: StatisticRepository
) {
    suspend operator fun invoke(type: StatType) {
        statsRepository.updateStatistic(type)
    }
}