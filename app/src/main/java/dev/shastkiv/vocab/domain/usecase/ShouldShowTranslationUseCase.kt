package dev.shastkiv.vocab.domain.usecase

import dev.shastkiv.vocab.domain.repository.WidgetSettingsRepository
import javax.inject.Inject

/**
 * Use Case для перевірки чи показувати переклад у віджеті
 */
class ShouldShowTranslationUseCase @Inject constructor(
    private val widgetSettingsRepository: WidgetSettingsRepository
) {
    suspend operator fun invoke(): Boolean {
        val settings = widgetSettingsRepository.getLatestWidgetSettings()
        return settings.showTranslation
    }
}