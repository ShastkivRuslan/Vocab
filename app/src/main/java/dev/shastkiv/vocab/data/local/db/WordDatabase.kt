package dev.shastkiv.vocab.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.shastkiv.vocab.data.local.dao.DailyStatisticDao
import dev.shastkiv.vocab.data.local.dao.WordDao
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.Word

@Database(
    entities = [Word::class, DailyStatistic::class],
    version = 2,
    exportSchema = false,

)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun dailyStatisticDao(): DailyStatisticDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE words ADD COLUMN mastery_score INTEGER NOT NULL DEFAULT 0")

        db.execSQL("ALTER TABLE words ADD COLUMN last_trained_at INTEGER NOT NULL DEFAULT 0")
    }
}