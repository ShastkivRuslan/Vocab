package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.Language // <-- Імпортуємо модель
import com.shastkiv.vocab.domain.model.WordData
import com.shastkiv.vocab.domain.repository.WordDetailsCacheRepository
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