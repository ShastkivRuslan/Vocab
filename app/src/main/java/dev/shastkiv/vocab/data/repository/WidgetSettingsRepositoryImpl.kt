package dev.shastkiv.vocab.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.shastkiv.vocab.domain.model.WidgetClickAction
import dev.shastkiv.vocab.domain.model.WidgetFilterMode
import dev.shastkiv.vocab.domain.model.WidgetSettings
import dev.shastkiv.vocab.domain.repository.WidgetSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : WidgetSettingsRepository {

    override val widgetSettings: Flow<WidgetSettings> = dataStore.data.map { preferences ->
        val frequency = preferences[Keys.UPDATE_FREQUENCY] ?: DEFAULT_FREQUENCY
        val showTranslation = preferences[Keys.SHOW_TRANSLATION] ?: DEFAULT_SHOW_TRANSLATION

        // Відновлюємо Enums з рядків, або беремо дефолтні
        val filterModeName = preferences[Keys.FILTER_MODE]
        val filterMode = try {
            if (filterModeName != null) WidgetFilterMode.valueOf(filterModeName) else DEFAULT_FILTER_MODE
        } catch (e: IllegalArgumentException) {
            DEFAULT_FILTER_MODE
        }

        val clickActionName = preferences[Keys.CLICK_ACTION]
        val clickAction = try {
            if (clickActionName != null) WidgetClickAction.valueOf(clickActionName) else DEFAULT_CLICK_ACTION
        } catch (e: IllegalArgumentException) {
            DEFAULT_CLICK_ACTION
        }

        val langCode = preferences[Keys.SPECIFIC_LANGUAGE_CODE]

        WidgetSettings(
            updateFrequencyMinutes = frequency,
            showTranslation = showTranslation,
            filterMode = filterMode,
            clickAction = clickAction,
            specificLanguageCode = langCode
        )
    }

    override suspend fun updateUpdateFrequency(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.UPDATE_FREQUENCY] = minutes
        }
    }

    override suspend fun updateShowTranslation(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_TRANSLATION] = show
        }
    }

    override suspend fun updateFilterMode(mode: WidgetFilterMode) {
        dataStore.edit { preferences ->
            preferences[Keys.FILTER_MODE] = mode.name // Зберігаємо як String
        }
    }

    override suspend fun updateClickAction(action: WidgetClickAction) {
        dataStore.edit { preferences ->
            preferences[Keys.CLICK_ACTION] = action.name // Зберігаємо як String
        }
    }

    override suspend fun updateSpecificLanguageCode(code: String?) {
        dataStore.edit { preferences ->
            if (code == null) {
                preferences.remove(Keys.SPECIFIC_LANGUAGE_CODE)
            } else {
                preferences[Keys.SPECIFIC_LANGUAGE_CODE] = code
            }
        }
    }

    override suspend fun getLatestWidgetSettings(): WidgetSettings {
        return widgetSettings.first()
    }

    private object Keys {
        val UPDATE_FREQUENCY = intPreferencesKey("widget_update_frequency")
        val SHOW_TRANSLATION = booleanPreferencesKey("widget_show_translation")
        val FILTER_MODE = stringPreferencesKey("widget_filter_mode")
        val CLICK_ACTION = stringPreferencesKey("widget_click_action")
        val SPECIFIC_LANGUAGE_CODE = stringPreferencesKey("widget_specific_language_code")
    }

    companion object {
        private const val DEFAULT_FREQUENCY = 30
        private const val DEFAULT_SHOW_TRANSLATION = true
        private val DEFAULT_FILTER_MODE = WidgetFilterMode.ALL
        private val DEFAULT_CLICK_ACTION = WidgetClickAction.OPEN_APP
    }
}