package dev.shastkiv.vocab.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.shastkiv.vocab.domain.model.enums.WordType

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

    @ColumnInfo(name = "correct_count")
    val correctAnswerCount: Int = 0,

    @ColumnInfo(name = "wrong_count")
    val wrongAnswerCount: Int = 0,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "word_type")
    val wordType: WordType = WordType.FREE,

    @ColumnInfo(name = "is_word_added")
    val isWordAdded: Boolean = true,

    @ColumnInfo(name = "ai_data_json")
    val aiDataJson: String? = null,

    @ColumnInfo(name = "mastery_score")
    val masteryScore: Int = 0,

    @ColumnInfo(name = "last_trained_at")
    val lastTrainedAt: Long = System.currentTimeMillis()
) {
    fun hasAIData(): Boolean = aiDataJson != null
    fun getAIData(): WordData? = aiDataJson?.let { WordData.fromJson(it) }
}

