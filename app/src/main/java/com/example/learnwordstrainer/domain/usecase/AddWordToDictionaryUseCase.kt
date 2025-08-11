package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.repository.WordRepository
import javax.inject.Inject

class AddWordToDictionaryUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(englishWord: String, translation: String) {
        if (englishWord.isNotBlank() && translation.isNotBlank()) {
            repository.addWord(englishWord, translation)
        }
    }
}