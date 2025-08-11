package com.example.learnwordstrainer.domain.repository

import com.example.learnwordstrainer.domain.model.WordData

interface WordDetailsCacheRepository {
    suspend fun getWordDetails(word: String): Result<WordData>
}