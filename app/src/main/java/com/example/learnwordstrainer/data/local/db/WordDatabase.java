package com.example.learnwordstrainer.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.learnwordstrainer.data.local.dao.WordDao;
import com.example.learnwordstrainer.domain.model.Word;

@Database(entities = {Word.class}, version = 2)
public abstract class WordDatabase extends RoomDatabase {
    private static volatile WordDatabase INSTANCE;
    private static final String DATABASE_NAME = "words.db";

    public abstract WordDao wordDao();

    public static WordDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    WordDatabase.class,
                                    DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
