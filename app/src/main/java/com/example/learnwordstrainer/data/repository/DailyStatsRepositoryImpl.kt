package com.example.learnwordstrainer.data.repository

import com.example.learnwordstrainer.data.local.dao.DailyStatisticDao
import com.example.learnwordstrainer.domain.model.DailyStatistic
import com.example.learnwordstrainer.domain.repository.DailyStatsRepository
import com.example.learnwordstrainer.domain.usecase.StatType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val statsDao: DailyStatisticDao
) : DailyStatsRepository {

    private fun getTodayDateString(): String = LocalDate.now().toString()

    override fun getTodayStatistic() = statsDao.getStatisticForDate(getTodayDateString())

    override suspend fun updateStatistic(type: StatType) {
        withContext(Dispatchers.IO) {
            val todayDate = getTodayDateString()
            val currentStats = statsDao.getStatisticForDate(todayDate).firstOrNull() ?: DailyStatistic(date = todayDate)

            val newStats = when (type) {
                StatType.WORD_ASKED -> currentStats.copy(wordsAsked = currentStats.wordsAsked + 1)
                StatType.WORD_ADDED -> currentStats.copy(wordsAdded = currentStats.wordsAdded + 1)
                StatType.CORRECT_ANSWER -> currentStats.copy(correctAnswers = currentStats.correctAnswers + 1)
                StatType.WRONG_ANSWER -> currentStats.copy(wrongAnswers = currentStats.wrongAnswers + 1)
            }

            statsDao.upsertStatistic(newStats)
        }
    }
}