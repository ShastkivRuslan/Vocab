package com.shastkiv.vocab.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.shastkiv.vocab.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val hasDismissedNotificationPermission: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.DISMISSED_NOTIFICATION_REQUEST] ?: NOT_DISMISSED
    }

    override suspend fun setNotificationPermissionDismissed(dismissed: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DISMISSED_NOTIFICATION_REQUEST] = dismissed
        }
    }

    override val hasDismissedOverlayPermission: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.DISMISSED_OVERLAY_REQUEST] ?: NOT_DISMISSED
    }

    override suspend fun setOverlayPermissionDismissed(dismissed: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DISMISSED_OVERLAY_REQUEST] = dismissed
        }
    }

    private object Keys {
        val DISMISSED_NOTIFICATION_REQUEST = booleanPreferencesKey("dismissed_notification_request")
        val DISMISSED_OVERLAY_REQUEST = booleanPreferencesKey("dismissed_overlay_request")
    }

    companion object {
        const val NOT_DISMISSED = false
    }
}