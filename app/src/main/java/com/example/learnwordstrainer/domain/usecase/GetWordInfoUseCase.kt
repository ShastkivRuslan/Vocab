package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.model.Language // <-- Імпортуємо модель
import com.example.learnwordstrainer.domain.model.WordData
import com.example.learnwordstrainer.domain.repository.WordDetailsCacheRepository
import javax.inject.Inject

class GetWordInfoUseCase @Inject constructor(
    private val repository: WordDetailsCacheRepository
) {
    suspend operator fun invoke(
        word: String,
        sourceLanguage: Language,
        targetLanguage: Language
    ): Result<WordData> {
        if (word.isBlank()) {
            return Result.failure(IllegalArgumentException("Слово не може бути порожнім"))
        }
        return repository.getWordDetails(word, sourceLanguage, targetLanguage)
    }
}