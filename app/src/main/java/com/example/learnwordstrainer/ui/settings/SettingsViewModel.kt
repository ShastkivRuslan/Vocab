package com.example.learnwordstrainer.ui.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import com.example.learnwordstrainer.domain.model.ThemeMode
import com.example.learnwordstrainer.data.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val themeRepository =
        ThemeRepository(application)

    private val _currentTheme = MutableStateFlow(themeRepository.themeMode)
    val currentTheme: StateFlow<Int> = _currentTheme.asStateFlow()

    fun setTheme(themeMode: ThemeMode) {
        val mode = when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        themeRepository.saveThemeMode(mode)
        _currentTheme.value = mode
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}