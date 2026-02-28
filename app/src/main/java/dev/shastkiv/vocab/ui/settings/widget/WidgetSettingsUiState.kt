package dev.shastkiv.vocab.ui.settings.widget

import dev.shastkiv.vocab.domain.model.WidgetSettings

sealed interface WidgetSettingsUiState {
    object Loading : WidgetSettingsUiState

    data class Success(
        val isWidgetAdded: Boolean,
        val settings: WidgetSettings?
    ) : WidgetSettingsUiState
}