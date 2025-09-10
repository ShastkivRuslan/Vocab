package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.di.IoDispatcher
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.repository.WordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetWordForWidgetUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val getLanguageSettingsUseCase: GetLanguageSettingsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Word? = withContext(ioDispatcher) {
        val languageSettings = getLanguageSettingsUseCase()
        val sourceLanguageCode = languageSettings.sourceLanguage.code

        wordRepository.getRandomWordForLanguage(sourceLanguageCode)
    }
}