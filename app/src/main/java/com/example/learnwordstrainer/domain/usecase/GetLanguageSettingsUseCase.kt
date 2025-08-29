package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.di.IoDispatcher
import com.example.learnwordstrainer.domain.model.LanguageSettings
import com.example.learnwordstrainer.domain.repository.LanguageRepository
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