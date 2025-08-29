package com.example.learnwordstrainer.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,

    @ColumnInfo(name = "source_word")
    val sourceWord: String,

    @ColumnInfo(name = "translation")
    val translation: String,

    @ColumnInfo(name = "source_language_code")
    val sourceLanguageCode: String,

    @ColumnInfo(name = "target_language_code")
    val targetLanguageCode: String,

    @ColumnInfo(name = "word_level")
    val wordLevel: String,

    @ColumnInfo(name = "correct_count")
    val correctAnswerCount: Int = 0,

    @ColumnInfo(name = "wrong_count")
    val wrongAnswerCount: Int = 0,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)