package com.shastkiv.vocab.domain.repository

import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.WordData

interface WordDetailsCacheRepository {
    suspend fun getWordDetails(
        word: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): Result<WordData>
}