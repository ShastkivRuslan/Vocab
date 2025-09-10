package com.shastkiv.vocab.domain.usecase

import com.shastkiv.vocab.di.IoDispatcher
import com.shastkiv.vocab.domain.repository.OnDeviceTranslator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslateUseCase @Inject constructor(
    private val onDeviceTranslator: OnDeviceTranslator,
    private val getLanguageSettingsUseCase: GetLanguageSettingsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(text: String): String = withContext(ioDispatcher) {
        val languageSettings = getLanguageSettingsUseCase()

        onDeviceTranslator.translate(
            text = text,
            sourceLangCode = languageSettings.sourceLanguage.code,
            targetLangCode = languageSettings.targetLanguage.code
        )
    }
}