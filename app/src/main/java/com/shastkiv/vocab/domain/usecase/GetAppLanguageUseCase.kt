package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.di.IoDispatcher
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.repository.LanguageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAppLanguageUseCase @Inject constructor(
    private val languageRepository: LanguageRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    fun observeAppLanguage(): Flow<Language> =
        languageRepository.languageSettings
            .map { it.appLanguage }
            .flowOn(ioDispatcher)

    suspend operator fun invoke(): Language = withContext(ioDispatcher) {
        languageRepository.getLatestLanguageSettings().appLanguage
    }
}