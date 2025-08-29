package com.example.learnwordstrainer.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.learnwordstrainer.data.local.dao.DailyStatisticDao
import com.example.learnwordstrainer.data.local.dao.WordDao
import com.example.learnwordstrainer.data.local.dao.WordDetailsCacheDao
import com.example.learnwordstrainer.domain.model.DailyStatistic
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.model.WordDetailsCache

@Database(
    entities = [Word::class, WordDetailsCache::class, DailyStatistic::class],
    version = 8,
    exportSchema = false
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun wordDetailsCacheDao(): WordDetailsCacheDao
    abstract fun dailyStatisticDao(): DailyStatisticDao
}