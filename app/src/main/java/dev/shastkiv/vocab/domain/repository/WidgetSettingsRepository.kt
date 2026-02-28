package dev.shastkiv.vocab.domain.repository

import dev.shastkiv.vocab.domain.model.WidgetClickAction
import dev.shastkiv.vocab.domain.model.WidgetFilterMode
import dev.shastkiv.vocab.domain.model.WidgetSettings
import kotlinx.coroutines.flow.Flow

interface WidgetSettingsRepository {

    val widgetSettings: Flow<WidgetSettings>

    suspend fun updateUpdateFrequency(minutes: Int)
    suspend fun updateShowTranslation(show: Boolean)
    suspend fun updateFilterMode(mode: WidgetFilterMode)
    suspend fun updateClickAction(action: WidgetClickAction)
    suspend fun updateSpecificLanguageCode(code: String?)

    suspend fun getLatestWidgetSettings(): WidgetSettings
}