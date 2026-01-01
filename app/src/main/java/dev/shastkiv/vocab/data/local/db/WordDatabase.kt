package dev.shastkiv.vocab.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.shastkiv.vocab.data.local.dao.DailyStatisticDao
import dev.shastkiv.vocab.data.local.dao.WordDao
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.Word

@Database(
    entities = [Word::class, DailyStatistic::class],
    version = 1,
    exportSchema = false
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun dailyStatisticDao(): DailyStatisticDao
}