package dev.shastkiv.vocab.ui.widget

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetStateKeys {
    val sourceWordKey = stringPreferencesKey("widget_source_word")
    val translationKey = stringPreferencesKey("widget_translation")
    val sourceFlagEmojiKey = stringPreferencesKey("widget_source_flag_emoji")
    val wordLevel = stringPreferencesKey("word_level")
    val updatedAt = longPreferencesKey("updated_at")
}