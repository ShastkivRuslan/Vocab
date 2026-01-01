package dev.shastkiv.vocab.domain.repository

import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.model.LanguageSettings
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {
    val languageSettings: Flow<LanguageSettings>

    suspend fun saveAppLanguage(language: Language)
    suspend fun saveTargetLanguage(language: Language)
    suspend fun saveSourceLanguage(language: Language)

    suspend fun getSourceLanguageName(): String
    suspend fun getTargetLanguageName(): String

    suspend fun getLatestLanguageSettings(): LanguageSettings
}