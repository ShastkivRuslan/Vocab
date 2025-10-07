package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveSourceLanguageUseCase @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(language: Language) {
        languageRepository.saveSourceLanguage(language)
    }
}