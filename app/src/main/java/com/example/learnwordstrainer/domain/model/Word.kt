package com.example.learnwordstrainer.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,

    @ColumnInfo(name = "english_word")
    val englishWord: String,

    @ColumnInfo(name = "translation")
    val translation: String,

    @ColumnInfo(name = "correct_count", defaultValue = "0")
    val correctAnswerCount: Int = 0,

    @ColumnInfo(name = "wrong_count", defaultValue = "0")
    val wrongAnswerCount: Int = 0
)