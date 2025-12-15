package com.shastkiv.vocab.data.repository

import com.shastkiv.vocab.data.local.dao.DailyStatisticDao
import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.model.enums.StatType
import com.shastkiv.vocab.domain.repository.StatisticRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class StatisticRepositoryImpl @Inject constructor(
    private val statsDao: DailyStatisticDao
) : StatisticRepository {

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

    override suspend fun getTodayStatisticOnce(): DailyStatistic? {
        return getTodayStatistic().first()
    }

    override suspend fun getCurrentStreak(): Int {
        return withContext(Dispatchers.IO) {
            val allStats = statsDao.getAllStatistics()

            var streak = 0
            var currentDate = LocalDate.now()

            while (true) {
                val stat = allStats.find { it.date == currentDate.toString() }

                if (stat != null && (stat.correctAnswers > 0 || stat.wrongAnswers > 0)) {
                    streak++
                    currentDate = currentDate.minusDays(1)
                } else {
                    break
                }
            }

            streak
        }
    }

    override suspend fun wasUserActiveToday(): Boolean {
        val today = getTodayStatisticOnce()
        return today != null && (today.correctAnswers > 0 || today.wrongAnswers > 0)
    }
}