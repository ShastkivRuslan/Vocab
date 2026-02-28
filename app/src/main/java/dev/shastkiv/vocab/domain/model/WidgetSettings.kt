package dev.shastkiv.vocab.domain.model

data class WidgetSettings(
    val updateFrequencyMinutes: Int,
    val showTranslation: Boolean,
    val filterMode: WidgetFilterMode,
    val clickAction: WidgetClickAction,
    val specificLanguageCode: String?
)

enum class WidgetFilterMode { ALL, LEARNING, HARD, MASTERED }
enum class WidgetClickAction { OPEN_APP, NEXT_WORD }
