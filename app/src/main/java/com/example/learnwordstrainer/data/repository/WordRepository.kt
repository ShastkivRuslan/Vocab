package com.example.learnwordstrainer.data.repository

import com.example.learnwordstrainer.domain.model.Word

interface WordRepository {
    suspend fun addWord(englishWord: String, translation: String)

    suspend fun getRandomWord(): Word?

    suspend fun getRandomTranslations(excludeId: Int): List<String>

    suspend fun wordExists(englishWord: String): Boolean

    suspend fun getWordCount(): Int

    suspend fun getLearnedWordsCount(): Int

    suspend fun updateScore(id: Int, correctCount: Int, wrongCount: Int)
}