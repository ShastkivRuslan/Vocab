package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.data.repository.WordDetailsCacheRepository
import com.example.learnwordstrainer.domain.model.WordData
import javax.inject.Inject

class GetWordInfoUseCase @Inject constructor(
    private val repository: WordDetailsCacheRepository
) {
    suspend operator fun invoke(word: String): Result<WordData> {
        if (word.isBlank()) {
            return Result.failure(IllegalArgumentException("Слово не може бути порожнім"))
        }
        return repository.getWordDetails(word)
    }
}