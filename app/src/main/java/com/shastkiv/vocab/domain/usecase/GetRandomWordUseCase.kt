package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class GetRandomWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(): Word? {
        return repository.getRandomWord()
    }
}