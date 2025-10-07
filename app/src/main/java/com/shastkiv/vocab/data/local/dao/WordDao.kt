package com.shastkiv.vocab.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.WordType
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

    @Query("SELECT COUNT(*) > 0 FROM words WHERE source_word = :sourceWord AND source_language_code = :sourceLanguageCode")
    suspend fun wordExists(sourceWord: String, sourceLanguageCode: String): Boolean

    @Query("SELECT COUNT(*) FROM words WHERE is_word_added = 1")
    suspend fun getWordCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE correct_count > 10 AND is_word_added = 1")
    suspend fun getLearnedWordsCount(): Int

    @Query("UPDATE words SET correct_count = :correctCount, wrong_count = :wrongCount WHERE _id = :id")
    suspend fun updateScore(id: Int, correctCount: Int, wrongCount: Int)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM words WHERE source_language_code = :sourceLanguageCode ORDER BY correct_count ASC, RANDOM() LIMIT 1")
    suspend fun getWordForRepetition(sourceLanguageCode: String): Word?

    @Query("SELECT translation FROM words WHERE target_language_code = :targetLanguageCode AND _id != :excludeId ORDER BY RANDOM() LIMIT 3")
    suspend fun getAnswerOptionsForWord(excludeId: Int, targetLanguageCode: String): List<String>

    @Query("SELECT * FROM words WHERE is_word_added = 1 ORDER BY added_at DESC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE source_language_code = :sourceCode AND is_word_added = 1 ORDER BY added_at DESC")
    fun getWordsByLanguage(sourceCode: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE source_word = :sourceWord AND source_language_code = :sourceLanguageCode LIMIT 1")
    suspend fun getCachedWord(sourceWord: String, sourceLanguageCode: String): Word?

    @Query("SELECT * FROM words WHERE _id = :id")
    suspend fun getWordById(id: Int): Word?

    @Query("UPDATE words SET word_type = :wordType, ai_data_json = :aiDataJson WHERE _id = :id")
    suspend fun updateWordWithAIData(id: Int, wordType: WordType, aiDataJson: String)

    @Query("SELECT COUNT(*) FROM words WHERE word_type = :wordType AND is_word_added = 1")
    suspend fun getWordCountByType(wordType: WordType): Int

    @Query("DELETE FROM words WHERE is_word_added = 0 AND added_at < :beforeTimestamp")
    suspend fun cleanOldCache(beforeTimestamp: Long)

    @Query("UPDATE words SET is_word_added = :isWordAdded, word_type = :wordType WHERE _id = :id")
    suspend fun updateToUserDictionary(id: Int, isWordAdded: Boolean, wordType: WordType)
}