package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class CheckIfWordExistsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(sourceWord: String, sourceLanguageCode: String): Boolean {

        if (sourceWord.isBlank() || sourceLanguageCode.isBlank()) {
            return false
        }
        return repository.wordExists(sourceWord, sourceLanguageCode)
    }
}