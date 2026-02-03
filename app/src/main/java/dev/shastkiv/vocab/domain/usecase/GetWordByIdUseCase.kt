package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

class GetWordByIdUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(id: Int): Word {
        return wordRepository.getWordById(id)
    }
}