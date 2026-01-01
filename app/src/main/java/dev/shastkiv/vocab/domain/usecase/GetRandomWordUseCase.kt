package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class GetRandomWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(): Word? {
        return repository.getRandomWord()
    }
}