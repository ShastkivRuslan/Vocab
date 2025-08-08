package com.example.learnwordstrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learnwordstrainer.domain.model.WordDetailsCache

@Dao
interface WordDetailsCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: WordDetailsCache)

    @Query("SELECT * FROM word_details_cache WHERE english_word = :word")
    suspend fun getCacheForWord(word: String): WordDetailsCache?
}