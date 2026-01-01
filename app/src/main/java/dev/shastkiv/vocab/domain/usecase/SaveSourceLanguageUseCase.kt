package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveSourceLanguageUseCase @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(language: Language) {
        languageRepository.saveSourceLanguage(language)
    }
}