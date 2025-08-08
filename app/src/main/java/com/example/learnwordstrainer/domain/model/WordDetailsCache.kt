package com.example.learnwordstrainer.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_details_cache")
data class WordDetailsCache(
    @PrimaryKey
    @ColumnInfo(name = "english_word")
    val englishWord: String,

    @ColumnInfo(name = "json_response")
    val jsonResponse: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)