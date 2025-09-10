package com.shastkiv.vocab.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_statistic")
data class DailyStatistic (
    @PrimaryKey
    val date: String = LocalDate.now().toString(),
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val wordsAdded: Int = 0,
    val wordsAsked: Int = 0,
    val totalMinutesPracticed: Int = 0
)