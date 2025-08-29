package com.example.learnwordstrainer.domain.repository

import com.example.learnwordstrainer.domain.model.DailyStatistic
import com.example.learnwordstrainer.domain.usecase.StatType
import kotlinx.coroutines.flow.Flow

interface DailyStatsRepository {

    fun getTodayStatistic(): Flow<DailyStatistic?>

    suspend fun updateStatistic(type: StatType)
}