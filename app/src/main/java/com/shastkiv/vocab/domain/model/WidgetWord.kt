package com.shastkiv.vocab.domain.model

data class WidgetWord(
    val sourceWord: String,
    val translation: String,
    val level: String?,
    val sourceLanguageCode: String
)