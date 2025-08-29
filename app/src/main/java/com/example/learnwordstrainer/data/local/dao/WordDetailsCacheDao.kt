package com.example.learnwordstrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learnwordstrainer.domain.model.WordDetailsCache

@Dao
interface WordDetailsCacheDao {
    @Query("SELECT * FROM word_details_cache WHERE source_word = :word AND source_language_code = :sourceLangCode AND target_language_code = :targetLangCode")
    suspend fun getCacheForWord(word: String, sourceLangCode: String, targetLangCode: String): WordDetailsCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WordDetailsCache)
}