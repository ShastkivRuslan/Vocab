package com.shastkiv.vocab.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeMode: Flow<Int>

    suspend fun saveThemeMode(themeMode: Int)
}
