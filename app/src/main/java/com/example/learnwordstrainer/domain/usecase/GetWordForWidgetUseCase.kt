package com.example.learnwordstrainer.domain.usecase

import com.example.learnwordstrainer.di.IoDispatcher
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.domain.repository.WordRepository
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