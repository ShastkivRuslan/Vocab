package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveTargetLanguageUseCase @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(language: Language) {
        languageRepository.saveTargetLanguage(language)
    }
}

