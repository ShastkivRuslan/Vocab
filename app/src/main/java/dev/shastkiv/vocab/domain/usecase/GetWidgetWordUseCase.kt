package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.model.WidgetFilterMode
import dev.shastkiv.vocab.domain.model.WidgetWord
import dev.shastkiv.vocab.domain.repository.LanguageRepository
import dev.shastkiv.vocab.domain.repository.WidgetSettingsRepository
import dev.shastkiv.vocab.domain.repository.WordRepository
import javax.inject.Inject

/**
 * Use Case для отримання слова для віджета з урахуванням налаштувань
 */
class GetWidgetWordUseCase @Inject constructor(
    private val wordRepository: WordRepository,
    private val widgetSettingsRepository: WidgetSettingsRepository,
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(): WidgetWord? {
        val settings = widgetSettingsRepository.getLatestWidgetSettings()
        val lang = languageRepository.getLatestLanguageSettings().sourceLanguage.code

        val word =  when (settings.filterMode) {
            WidgetFilterMode.ALL -> wordRepository.getRandomWordForLanguage(lang)
            WidgetFilterMode.LEARNING -> wordRepository.getRandomStableWord(lang)
            WidgetFilterMode.MASTERED -> wordRepository.getRandomMasteredWord(lang)
            WidgetFilterMode.HARD -> wordRepository.getRandomHardWord(lang)
        }

        return word?.let { w ->
            WidgetWord(
                sourceWord = w.sourceWord,
                translation = w.translation,
                level = w.getAIData()?.level,
                sourceLanguageCode = w.sourceLanguageCode
            )
        }
    }
}