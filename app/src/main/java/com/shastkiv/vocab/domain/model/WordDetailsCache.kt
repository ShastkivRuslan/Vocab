package com.shastkiv.vocab.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_details_cache")
data class WordDetailsCache(
    @PrimaryKey
    @ColumnInfo(name = "source_word")
    val sourceWord: String,

    @ColumnInfo(name = "source_language_code")
    val sourceLanguageCode: String,

    @ColumnInfo(name = "target_language_code")
    val targetLanguageCode: String,

    @ColumnInfo(name = "json_response")
    val jsonResponse: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()


)