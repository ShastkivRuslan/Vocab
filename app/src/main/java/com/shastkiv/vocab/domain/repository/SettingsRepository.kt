package com.shastkiv.vocab.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val hasDismissedNotificationPermission: Flow<Boolean>
    suspend fun setNotificationPermissionDismissed(dismissed: Boolean)

    val hasDismissedOverlayPermission: Flow<Boolean>
    suspend fun setOverlayPermissionDismissed(dismissed: Boolean)
}