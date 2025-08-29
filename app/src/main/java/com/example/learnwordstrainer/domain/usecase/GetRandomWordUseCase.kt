package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.repository.WordRepository
import javax.inject.Inject

class GetRandomWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(): Word? {
        return repository.getRandomWord()
    }
}