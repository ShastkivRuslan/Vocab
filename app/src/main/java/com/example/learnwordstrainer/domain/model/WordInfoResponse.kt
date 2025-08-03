package com.example.learnwordstrainer.domain.model

data class WordInfoResponse(
    val translation: String,
    val transcription: String,
    val partOfSpeech: String,
    val level: String,
    val examples: List<Example>
)
