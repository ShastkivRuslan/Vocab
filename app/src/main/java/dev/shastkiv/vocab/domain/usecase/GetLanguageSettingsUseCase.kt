package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.LanguageSettings
import dev.shastkiv.vocab.domain.repository.LanguageRepository
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