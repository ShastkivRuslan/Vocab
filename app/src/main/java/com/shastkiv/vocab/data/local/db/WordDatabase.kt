package com.shastkiv.vocab.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shastkiv.vocab.data.local.dao.DailyStatisticDao
import com.shastkiv.vocab.data.local.dao.WordDao
import com.shastkiv.vocab.data.local.dao.WordDetailsCacheDao
import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.WordDetailsCache

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