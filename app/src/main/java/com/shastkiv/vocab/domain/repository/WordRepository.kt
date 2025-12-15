package com.shastkiv.vocab.domain.repository

import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.enums.WordType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface WordRepository {
    suspend fun addWord(word: Word)

    suspend fun getRandomWord(): Word?

    suspend fun getRandomTranslations(excludeId: Int): List<String>

    suspend fun wordExists(sourceWord: String, sourceLanguageCode: String): Boolean

    fun getWordCount(): Flow<Int>

    fun getLearnedWordsCount(): Flow<Int>

    suspend fun updateScore(id: Int, correctCount: Int, wrongCount: Int)

    fun getWords(sourceLanguageCode: String): Flow<List<Word>>

    suspend fun deleteWord(word: Word)

    suspend fun getRandomWordForLanguage(languageCode: String): Word?

    suspend fun getWordForRepetition(sourceLanguageCode: String): Word?

    suspend fun getAnswerOptionsForWord(wordToRepeat: Word, targetLanguageCode: String): List<String>

    suspend fun getCachedWord(sourceWord: String, sourceLanguageCode: String): Word?

    suspend fun getWordById(id: Int): Word?

    suspend fun upgradeWordToAI(wordId: Int, aiDataJson: String, wordType: WordType)

    suspend fun getWordCountByType(wordType: WordType): Int

    suspend fun updateToUserDictionary(wordId: Int, wordType: WordType)

    fun getWordsNeedingRepetition(): Flow<Int>

    fun getWordsAddedBetween(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Word>>

}