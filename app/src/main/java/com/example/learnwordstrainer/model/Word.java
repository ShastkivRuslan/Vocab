package com.example.learnwordstrainer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo(name = "english_word")
    private String englishWord;

    @ColumnInfo(name = "translation")
    private String translation;

    @ColumnInfo(name = "correct_count", defaultValue = "0")
    private int correctAnswerCount;

    @ColumnInfo(name = "wrong_count", defaultValue = "0")
    private int wrongAnswerCount;

    public Word(String englishWord, String translation) {
        this.englishWord = englishWord;
        this.translation = translation;
        this.correctAnswerCount = 0;
        this.wrongAnswerCount = 0;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getEnglishWord() {
        return englishWord;
    }
    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }

    public String getTranslation() {
        return translation;
    }
    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }
    public void setCorrectAnswerCount(int correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
    }

    public int getWrongAnswerCount() {
        return wrongAnswerCount;
    }
    public void setWrongAnswerCount(int wrongAnswerCount) {
        this.wrongAnswerCount = wrongAnswerCount;
    }
}
