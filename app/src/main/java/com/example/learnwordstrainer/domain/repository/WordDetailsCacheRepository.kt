package com.example.learnwordstrainer.domain.repository

import com.example.learnwordstrainer.domain.model.Language
import com.example.learnwordstrainer.domain.model.WordData

interface WordDetailsCacheRepository {
    suspend fun getWordDetails(
        word: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): Result<WordData>
}