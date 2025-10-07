package com.shastkiv.vocab.data.repository

import com.shastkiv.vocab.data.local.dao.WordDao
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.WordType
import com.shastkiv.vocab.domain.repository.WordRepository
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
            wordDao.wordExists(sourceWord, sourceLanguageCode)
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

    override suspend fun getCachedWord(sourceWord: String, sourceLanguageCode: String): Word? {
        return withContext(Dispatchers.IO) {
            wordDao.getCachedWord(sourceWord, sourceLanguageCode)
        }
    }

    override suspend fun getWordById(id: Int): Word? {
        return withContext(Dispatchers.IO) {
            wordDao.getWordById(id)
        }
    }

    override suspend fun upgradeWordToAI(wordId: Int, aiDataJson: String, wordType: WordType) {
        withContext(Dispatchers.IO) {
            wordDao.updateWordWithAIData(
                id = wordId,
                wordType = wordType,
                aiDataJson = aiDataJson
            )
        }
    }

    override suspend fun getWordCountByType(wordType: WordType): Int {
        return withContext(Dispatchers.IO) {
            wordDao.getWordCountByType(wordType)
        }
    }

    override suspend fun updateToUserDictionary(wordId: Int, wordType: WordType) {
        withContext(Dispatchers.IO) {
            wordDao.updateToUserDictionary(wordId, true, wordType)
        }
    }
}