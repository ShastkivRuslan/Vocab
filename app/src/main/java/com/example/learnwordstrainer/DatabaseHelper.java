package com.example.learnwordstrainer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_WORDS = "words";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ENGLISH = "english_word";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_CORRECT_ANSWER_COUNT = "correct_count";
    public static final String COLUMN_WRONG_ANSWER_COUNT = "wrong_count";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ENGLISH + " TEXT, "
                + COLUMN_TRANSLATION + " TEXT, "
                + COLUMN_CORRECT_ANSWER_COUNT + " INTEGER DEFAULT 0, "
                + COLUMN_WRONG_ANSWER_COUNT + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        onCreate(db);
    }

    public void addWord(String english, String translation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENGLISH, english);
        values.put(COLUMN_TRANSLATION, translation);
        values.put(COLUMN_CORRECT_ANSWER_COUNT, 0);
        values.put(COLUMN_WRONG_ANSWER_COUNT, 0);
        db.insert(TABLE_WORDS, null, values);
        db.close();
    }

    public Cursor getRandomWord() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_WORDS + " ORDER BY RANDOM() LIMIT 1";
        return db.rawQuery(query, null);
    }

    public List<String> getRandomTranslations(int excludeId) {
        List<String> translations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_TRANSLATION + " FROM " + TABLE_WORDS +
                " WHERE " + COLUMN_ID + " != ? ORDER BY RANDOM() LIMIT 3";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(excludeId)});

        if (cursor.moveToFirst()) {
            do {
                translations.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return translations;
    }

    public boolean wordExists(String englishWord) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORDS, new String[]{COLUMN_ID},
                COLUMN_ENGLISH + "=?", new String[]{englishWord},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public int getWordCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORDS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getLearnedWordsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_WORDS +
                " WHERE " + COLUMN_CORRECT_ANSWER_COUNT + " - " + COLUMN_WRONG_ANSWER_COUNT + " >= 10";
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public void updateScore(int id, int correctCount, int wrongCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CORRECT_ANSWER_COUNT, correctCount);
        values.put(COLUMN_WRONG_ANSWER_COUNT, wrongCount);
        db.update(TABLE_WORDS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}

