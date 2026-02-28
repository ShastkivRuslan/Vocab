package dev.shastkiv.vocab.ui.settings.main

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.enums.ThemeMode
import dev.shastkiv.vocab.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val themeRepository: ThemeRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onThemeSettingClick() {
        _showThemeDialog.value = true
    }

    fun onDismissThemeDialog() {
        _showThemeDialog.value = false
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        // Додаємо прапор, якщо викликаємо не з Activity (наприклад, з вашого Service)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Якщо з якоїсь причини налаштування не відкриваються
            Log.e("Vocab", "Не вдалося відкрити налаштування доступності", e)
        }
    }

    fun clearError() {
        _error.value = null
    }

    val currentTheme: StateFlow<Int> = themeRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val mode = when (themeMode) {
                    ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                themeRepository.saveThemeMode(mode)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = context.getString(R.string.error_failed_to_save_theme)
            }
        }
    }
}