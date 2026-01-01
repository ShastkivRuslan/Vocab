package dev.shastkiv.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.shastkiv.vocab.domain.model.DailyStatistic
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyStatisticDao {

    @Query("SELECT * FROM daily_statistic where date =:date")
    fun getStatisticForDate(date: String = LocalDate.now().toString()) : Flow<DailyStatistic?>

    @Upsert
    suspend fun upsertStatistic(statistic: DailyStatistic)

    @Query("SELECT * FROM daily_statistic ORDER BY date DESC")
    suspend fun getAllStatistics(): List<DailyStatistic>

    @Query(" SELECT EXISTS( SELECT 1 FROM daily_statistic WHERE date = :date AND (correctAnswers > 0 OR wrongAnswers > 0))")
    suspend fun wasActiveOnDate(date: String = LocalDate.now().toString()): Boolean
}