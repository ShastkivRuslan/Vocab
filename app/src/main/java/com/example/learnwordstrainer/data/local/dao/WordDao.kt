package com.example.learnwordstrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learnwordstrainer.domain.model.Word

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWord(word: Word)

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Word?

    @Query("SELECT translation FROM words WHERE _id != :excludeId ORDER BY RANDOM() LIMIT 3")
    suspend fun getRandomTranslations(excludeId: Int): List<String>

    @Query("SELECT COUNT(*) > 0 FROM words WHERE english_word = :englishWord")
    suspend fun wordExists(englishWord: String): Boolean

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE correct_count > 10")
    suspend fun getLearnedWordsCount(): Int

    @Query("UPDATE words SET correct_count = :correctCount, wrong_count = :wrongCount WHERE _id = :id")
    suspend fun updateScore(id: Int, correctCount: Int, wrongCount: Int)
}