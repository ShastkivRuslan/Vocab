package com.example.learnwordstrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learnwordstrainer.domain.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWord(word: Word)

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Word?

    @Query("SELECT * FROM words WHERE source_language_code = :languageCode ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWordForLanguage(languageCode: String): Word?

    @Query("SELECT translation FROM words WHERE _id != :excludeId ORDER BY RANDOM() LIMIT 3")
    suspend fun getRandomTranslations(excludeId: Int): List<String>

    @Query("SELECT COUNT(*) > 0 FROM words WHERE source_word = :sourceWord")
    suspend fun wordExists(sourceWord: String): Boolean

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE correct_count > 10")
    suspend fun getLearnedWordsCount(): Int

    @Query("UPDATE words SET correct_count = :correctCount, wrong_count = :wrongCount WHERE _id = :id")
    suspend fun updateScore(id: Int, correctCount: Int, wrongCount: Int)

    @Query("SELECT * FROM words ORDER BY added_at DESC")
    fun getAllWords(): Flow<List<Word>>

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM words WHERE source_language_code = :sourceCode ORDER BY added_at DESC")
    fun getWordsByLanguage(sourceCode: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE source_language_code = :sourceLanguageCode ORDER BY correct_count ASC, RANDOM() LIMIT 1")
    suspend fun getWordForRepetition(sourceLanguageCode: String): Word?

    @Query("SELECT translation FROM words WHERE target_language_code = :targetLanguageCode AND _id != :excludeId ORDER BY RANDOM() LIMIT 3")
    suspend fun getAnswerOptionsForWord(excludeId: Int, targetLanguageCode: String): List<String>
}