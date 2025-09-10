package com.shastkiv.vocab.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.domain.repository.LanguageRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "global_settings")

@Singleton
class LanguageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : LanguageRepository {

    private val dataStore = context.settingsDataStore

    override val languageSettings: Flow<LanguageSettings> = dataStore.data.map { preferences ->
        val appLangJson = preferences[Keys.APP_LANGUAGE]
        val targetLangJson = preferences[Keys.TARGET_LANGUAGE]
        val sourceLangJson = preferences[Keys.SOURCE_LANGUAGE]

        LanguageSettings(
            appLanguage = gson.fromJson(appLangJson, Language::class.java) ?: DEFAULT_APP_LANGUAGE,
            targetLanguage = gson.fromJson(targetLangJson, Language::class.java) ?: DEFAULT_TARGET_LANGUAGE,
            sourceLanguage = gson.fromJson(sourceLangJson, Language::class.java) ?: DEFAULT_SOURCE_LANGUAGE
        )
    }

    override suspend fun saveAppLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_LANGUAGE] = gson.toJson(language)
        }
    }

    override suspend fun saveTargetLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[Keys.TARGET_LANGUAGE] = gson.toJson(language)
        }
    }

    override suspend fun saveSourceLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[Keys.SOURCE_LANGUAGE] = gson.toJson(language)
        }
    }

    override suspend fun getSourceLanguageName(): String {
        return languageSettings.first().sourceLanguage.name
    }

    override suspend fun getTargetLanguageName(): String {
        return languageSettings.first().targetLanguage.name
    }

    override suspend fun getLatestLanguageSettings(): LanguageSettings {
        return languageSettings.first()
    }


    private object Keys {
        val APP_LANGUAGE = stringPreferencesKey("app_language_json")
        val TARGET_LANGUAGE = stringPreferencesKey("target_language_json")
        val SOURCE_LANGUAGE = stringPreferencesKey("source_language_json")
    }

    companion object {
        private val DEFAULT_APP_LANGUAGE = Language("uk", "Українська", "🇺🇦")
        private val DEFAULT_TARGET_LANGUAGE = Language("uk", "Українська", "🇺🇦")
        private val DEFAULT_SOURCE_LANGUAGE = Language("en", "English", "🇬🇧")
    }
}