package dev.shastkiv.vocab.ui.initialsetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shastkiv.vocab.domain.model.AvailableLanguages
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.usecase.CompleteInitialSetupUseCase
import dev.shastkiv.vocab.domain.usecase.GetAppLanguageUseCase
import dev.shastkiv.vocab.domain.usecase.SaveAppLanguageUseCase
import dev.shastkiv.vocab.domain.usecase.SaveTranslationLanguagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitialSetupViewModel @Inject constructor(
    private val saveAppLanguageUseCase: SaveAppLanguageUseCase,
    getAppLanguageUseCase: GetAppLanguageUseCase,
    private val saveTranslationLanguageUseCase: SaveTranslationLanguagesUseCase,
    private val completeInitialSetupUseCase: CompleteInitialSetupUseCase
) : ViewModel() {

    val currentAppLanguage = getAppLanguageUseCase.observeAppLanguage()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AvailableLanguages.DEFAULT_ENGLISH
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun saveAppLanguage(language: Language) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                saveAppLanguageUseCase(language)
            } catch (e: Exception) {
                _error.value = "Failed to save app language"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveTranslationLanguages(sourceLanguage: Language, targetLanguage: Language) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                saveTranslationLanguageUseCase(sourceLanguage, targetLanguage)
            } catch (e: IllegalArgumentException) {
                _error.value = "Source and target languages must be different"
            } catch (e: Exception) {
                _error.value = "Failed to save translation languages"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeSetup() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                completeInitialSetupUseCase()
            } catch (e: Exception) {
                _error.value = "Failed to complete setup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}