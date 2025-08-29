package com.example.learnwordstrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.learnwordstrainer.domain.model.DailyStatistic
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyStatisticDao {

    @Query("SELECT * FROM daily_statistic where date =:date")
    fun getStatisticForDate(date: String = LocalDate.now().toString()) : Flow<DailyStatistic?>

    @Upsert
    suspend fun upsertStatistic(statistic: DailyStatistic)
}