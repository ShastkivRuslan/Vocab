package com.shastkiv.vocab.ui.settings.language

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.R
import com.shastkiv.vocab.di.IoDispatcher
import com.shastkiv.vocab.domain.model.AvailableLanguages
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.domain.repository.LanguageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

enum class LanguageDialogType {
    APP, TARGET, SOURCE
}

@HiltViewModel
class LanguageSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val languageRepository: LanguageRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _dialogType = MutableStateFlow<LanguageDialogType?>(null)
    val dialogType: StateFlow<LanguageDialogType?> = _dialogType.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun showDialog(type: LanguageDialogType) {
        _dialogType.value = type
    }

    fun dismissDialog() {
        _dialogType.value = null
    }

    fun clearError() {
        _error.value = null
    }

    val availableLanguages: List<Language> = AvailableLanguages.list

    val currentSettings: StateFlow<LanguageSettings> = languageRepository.languageSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = LanguageSettings(
                appLanguage = AvailableLanguages.DEFAULT_UKRAINIAN,
                targetLanguage = AvailableLanguages.DEFAULT_UKRAINIAN,
                sourceLanguage = AvailableLanguages.DEFAULT_ENGLISH
            )
        )

    fun saveAppLanguage(language: Language) {
        saveLanguageSetting { languageRepository.saveAppLanguage(language) }
    }

    fun saveTargetLanguage(language: Language) {
        saveLanguageSetting { languageRepository.saveTargetLanguage(language) }
    }

    fun saveSourceLanguage(language: Language) {
        saveLanguageSetting { languageRepository.saveSourceLanguage(language) }
    }

    private fun saveLanguageSetting(action: suspend () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            try {
                action()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = context.getString(R.string.error_failed_to_save_language)
            }
        }
    }
}