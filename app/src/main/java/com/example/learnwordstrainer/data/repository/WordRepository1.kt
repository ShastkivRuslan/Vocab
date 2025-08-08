package com.example.learnwordstrainer.data.repository

import com.example.learnwordstrainer.data.local.dao.WordDao
import com.example.learnwordstrainer.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {

    override suspend fun addWord(englishWord: String, translation: String) {
        withContext(Dispatchers.IO) {
            val word = Word(englishWord = englishWord, translation = translation)
            wordDao.addWord(word)
        }
    }

    override suspend fun getRandomWord(): Word? {
        return withContext(Dispatchers.IO) {
            wordDao.getRandomWord()
        }
    }

    override suspend fun getRandomTranslations(excludeId: Int): List<String> {
        return withContext(Dispatchers.IO) {
            wordDao.getRandomTranslations(excludeId)
        }
    }

    override suspend fun wordExists(englishWord: String): Boolean {
        return withContext(Dispatchers.IO) {
            wordDao.wordExists(englishWord)
        }
    }

    override suspend fun getWordCount(): Int {
        return withContext(Dispatchers.IO) {
            wordDao.getWordCount()
        }
    }

    override suspend fun getLearnedWordsCount(): Int {
        return withContext(Dispatchers.IO) {
            wordDao.getLearnedWordsCount()
        }
    }

    override suspend fun updateScore(id: Int, correctCount: Int, wrongCount: Int) {
        withContext(Dispatchers.IO) {
            wordDao.updateScore(id, correctCount, wrongCount)
        }
    }
}