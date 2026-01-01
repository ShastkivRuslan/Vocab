package dev.shastkiv.vocab.domain.model

data class LanguageSettings(
    val appLanguage: Language,
    val targetLanguage: Language,
    val sourceLanguage: Language
)