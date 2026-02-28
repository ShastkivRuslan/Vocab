package dev.shastkiv.vocab.ui.settings.widget

sealed class WidgetSettingsEvent {
    object RequestPinWidget : WidgetSettingsEvent()
}