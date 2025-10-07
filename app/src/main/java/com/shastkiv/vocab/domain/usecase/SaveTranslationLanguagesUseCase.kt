package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveTranslationLanguagesUseCase @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(sourceLanguage: Language, targetLanguage: Language) {
        require(sourceLanguage != targetLanguage) {
            "Source and target languages must be different"
        }

        languageRepository.saveSourceLanguage(sourceLanguage)
        languageRepository.saveTargetLanguage(targetLanguage)
    }
}