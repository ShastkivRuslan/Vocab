package com.example.learnwordstrainer.data.repository

import com.example.learnwordstrainer.data.local.dao.WordDao
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {


    override suspend fun addWord(word: Word) {
        withContext(Dispatchers.IO) {
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

    override suspend fun getRandomWordForLanguage(languageCode: String): Word? {
        return withContext(Dispatchers.IO) {
            wordDao.getRandomWordForLanguage(languageCode)
        }
    }

    override suspend fun wordExists(sourceWord: String, sourceLanguageCode: String): Boolean {
        return withContext(Dispatchers.IO) {
            wordDao.wordExists(sourceWord)
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

    override fun getWords(sourceLanguageCode: String): Flow<List<Word>> {
        return if (sourceLanguageCode.equals("all", ignoreCase = true) || sourceLanguageCode.isBlank()) {
            wordDao.getAllWords()
        } else {
            wordDao.getWordsByLanguage(sourceLanguageCode)
        }
    }

    override suspend fun deleteWord(word: Word) {
        withContext(Dispatchers.IO) {
            wordDao.deleteWord(word)
        }
    }

    override suspend fun getWordForRepetition(sourceLanguageCode: String): Word? {
        return withContext(Dispatchers.IO) {
            wordDao.getWordForRepetition(sourceLanguageCode)
        }
    }

    override suspend fun getAnswerOptionsForWord(wordToRepeat: Word, targetLanguageCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            wordDao.getAnswerOptionsForWord(
                excludeId = wordToRepeat.id,
                targetLanguageCode = targetLanguageCode
            )
        }
    }
}