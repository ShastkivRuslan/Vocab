package com.example.learnwordstrainer.repository;

import android.app.Application;
import android.content.Context;

import com.example.learnwordstrainer.dao.WordDao;
import com.example.learnwordstrainer.db.WordDatabase;
import com.example.learnwordstrainer.model.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordRepository {
    private final WordDao wordDao;
    private final ExecutorService executorService;

    public WordRepository(Context context) {
        WordDatabase db = WordDatabase.getDatabase(context);
        wordDao = db.wordDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void addWord(String englishWord, String translation) {
        executorService.execute(() -> {
            Word word = new Word(englishWord, translation);
            wordDao.addWord(word);
        });
    }

    public Word getRandomWord() {
        try {
            return executorService.submit(wordDao::getRandomWord).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getRandomTranslations(int excludeId) {
        try {
            return executorService.submit(() -> wordDao.getRandomTranslations(excludeId)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean wordExists(String englishWord) {
        try {
            return executorService.submit(() -> wordDao.wordExists(englishWord)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getWordCount() {
        try {
            return executorService.submit(wordDao::getWordCount).get();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getLearnedWordsCount() {
        try {
            return executorService.submit(wordDao::getLearnedWordsCount).get();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void updateScore(int id, int correctCount, int wrongCount) {
        executorService.execute(() ->
                wordDao.updateScore(id, correctCount, wrongCount)
        );
    }
}
