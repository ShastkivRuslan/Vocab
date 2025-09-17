package com.shastkiv.vocab.domain.repository

import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.usecase.StatType
import kotlinx.coroutines.flow.Flow

interface DailyStatsRepository {

    fun getTodayStatistic(): Flow<DailyStatistic?>

    suspend fun updateStatistic(type: StatType)
}