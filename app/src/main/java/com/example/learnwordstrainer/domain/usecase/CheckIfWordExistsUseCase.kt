package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.data.repository.WordRepository
import javax.inject.Inject

class CheckIfWordExistsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(englishWord: String): Boolean {
        return repository.wordExists(englishWord)
    }
}