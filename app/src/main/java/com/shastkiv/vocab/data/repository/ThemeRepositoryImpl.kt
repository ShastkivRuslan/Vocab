package com.shastkiv.vocab.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shastkiv.vocab.domain.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {

    override val themeMode: Flow<Int> = dataStore.data.map { preferences ->
        preferences[Keys.THEME_MODE] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    override suspend fun saveThemeMode(themeMode: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode
        }
    }

    private object Keys {
        val THEME_MODE = intPreferencesKey("theme_preference")
    }

    companion object {
        private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")
    }
}
