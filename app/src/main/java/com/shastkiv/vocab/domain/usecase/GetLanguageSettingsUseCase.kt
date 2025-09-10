package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.di.IoDispatcher
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.domain.repository.LanguageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLanguageSettingsUseCase @Inject constructor(
    private val languageRepository: LanguageRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): LanguageSettings = withContext(ioDispatcher) {
        languageRepository.getLatestLanguageSettings()
    }
}