package com.shastkiv.vocab.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.shastkiv.vocab.domain.model.BubblePosition
import com.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BubbleSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BubbleSettingsRepository {

    private val dataStore = context.settingsDataStore

    override val position: Flow<BubblePosition> = dataStore.data.map { prefs ->
        val x = prefs[Keys.BUBBLE_X] ?: DEFAULT_POSITION.x
        val y = prefs[Keys.BUBBLE_Y] ?: DEFAULT_POSITION.y
        BubblePosition(x, y)
    }

    override val isBubbleEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_BUBBLE_ENABLED] ?: DEFAULT_IS_ENABLED
    }

    override val bubbleSize: Flow<Float> = dataStore.data.map { prefs ->
        prefs[Keys.BUBBLE_SIZE] ?: DEFAULT_SIZE
    }

    override val bubbleTransparency: Flow<Float> = dataStore.data.map { prefs ->
        prefs[Keys.BUBBLE_TRANSPARENCY] ?: DEFAULT_TRANSPARENCY
    }

    override val isVibrationEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_VIBRATION_ENABLED] ?: DEFAULT_IS_VIBRATION_ENABLED
    }

    override val autoHideAppList: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.AUTO_HIDE_APP_LIST] ?: DEFAULT_AUTO_HIDE_APP_LIST
    }

    override suspend fun savePosition(x: Int, y: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.BUBBLE_X] = x
            prefs[Keys.BUBBLE_Y] = y
        }
    }

    override suspend fun setBubbleEnabled(isEnabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_BUBBLE_ENABLED] = isEnabled
        }
    }

    override suspend fun setBubbleSize(size: Float) {
        dataStore.edit { prefs ->
            prefs[Keys.BUBBLE_SIZE] = size
        }
    }

    override suspend fun setBubbleTransparency(transparency: Float) {
        dataStore.edit { prefs ->
            prefs[Keys.BUBBLE_TRANSPARENCY] = transparency
        }
    }

    override suspend fun setVibrationEnabled(isEnabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_VIBRATION_ENABLED] = isEnabled
        }
    }

    override suspend fun addAppToAutoHideList(packageName: String) {
        dataStore.edit { prefs ->
            val currentList = prefs[Keys.AUTO_HIDE_APP_LIST] ?: emptySet()
            prefs[Keys.AUTO_HIDE_APP_LIST] = currentList + packageName
        }
    }

    override suspend fun removeAppFromAutoHideList(packageName: String) {
        dataStore.edit { prefs ->
            val currentList = prefs[Keys.AUTO_HIDE_APP_LIST] ?: emptySet()
            prefs[Keys.AUTO_HIDE_APP_LIST] = currentList - packageName
        }
    }

    private object Keys {
        val BUBBLE_X = intPreferencesKey("bubble_x")
        val BUBBLE_Y = intPreferencesKey("bubble_y")
        val IS_BUBBLE_ENABLED = booleanPreferencesKey("is_bubble_enabled")
        val BUBBLE_SIZE = floatPreferencesKey("bubble_size")
        val BUBBLE_TRANSPARENCY = floatPreferencesKey("bubble_transparency")
        val IS_VIBRATION_ENABLED = booleanPreferencesKey("is_vibration_enabled")
        val AUTO_HIDE_APP_LIST = stringSetPreferencesKey("auto_hide_app_list")
    }

    companion object {
        private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "bubble_settings_main")

        val DEFAULT_POSITION = BubblePosition(20, 100)
        const val DEFAULT_IS_ENABLED = true
        const val DEFAULT_SIZE = 40f
        const val DEFAULT_TRANSPARENCY = 100f // 0-100%
        const val DEFAULT_IS_VIBRATION_ENABLED = true
        val DEFAULT_AUTO_HIDE_APP_LIST = emptySet<String>()
    }
}
