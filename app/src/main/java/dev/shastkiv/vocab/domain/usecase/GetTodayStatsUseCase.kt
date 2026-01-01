package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.repository.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayStatsUseCase @Inject constructor(
    private val statsRepository: StatisticRepository
) {

    operator fun invoke(): Flow<DailyStatistic?> {
        return statsRepository.getTodayStatistic()
    }
}