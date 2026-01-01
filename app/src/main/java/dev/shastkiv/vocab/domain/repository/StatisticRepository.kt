package dev.shastkiv.vocab.domain.repository

import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.enums.StatType
import kotlinx.coroutines.flow.Flow

interface StatisticRepository {

    fun getTodayStatistic(): Flow<DailyStatistic?>

    suspend fun updateStatistic(type: StatType)

    suspend fun getTodayStatisticOnce(): DailyStatistic?
    suspend fun getCurrentStreak(): Int
    suspend fun wasUserActiveToday(): Boolean
}