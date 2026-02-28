package dev.shastkiv.vocab.domain.model

data class WidgetWord(
    val sourceWord: String,
    val translation: String,
    val sourceLanguageCode: String,
    val level: String?
)