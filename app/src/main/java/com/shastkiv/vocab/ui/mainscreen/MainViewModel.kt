package com.shastkiv.vocab.ui.mainscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.R
import com.shastkiv.vocab.di.IoDispatcher
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.domain.model.UiError
import com.shastkiv.vocab.domain.repository.LanguageRepository
import com.shastkiv.vocab.domain.repository.ThemeRepository
import com.shastkiv.vocab.domain.repository.WordRepository
import com.shastkiv.vocab.utils.mapThrowableToUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

enum class ServiceType {
    NOTIFICATION, BUBBLE
}

sealed interface MainScreenEffect {
    data class StartService(val type: ServiceType) : MainScreenEffect
}

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    themeRepository: ThemeRepository,
    private val wordRepository: WordRepository,
    languageRepository: LanguageRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val themeMode: StateFlow<Int> = themeRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    val languageSettings: StateFlow<LanguageSettings> = languageRepository.languageSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LanguageSettings(
                appLanguage = Language("uk", "Українська", "🇺🇦"),
                targetLanguage = Language("uk", "Українська", "🇺🇦"),
                sourceLanguage = Language("en", "English", "🇬🇧")
            )
        )

    data class StatisticsUiState(
        val totalWordsCount: Int = 0,
        val learnedPercentage: Int = 0,
        val isLoading: Boolean = true,
        val error: UiError? = null
    )

    private val _statisticsState = MutableStateFlow(StatisticsUiState())
    val statisticsState: StateFlow<StatisticsUiState> = _statisticsState.asStateFlow()

    private val _effect = Channel<MainScreenEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadStatistics()
    }

    fun onResume() {
        loadStatistics()
        startServicesIfPermissionsGranted()
    }

    private fun startServicesIfPermissionsGranted() = viewModelScope.launch(ioDispatcher) {

        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (hasNotificationPermission) {
            sendEffect(MainScreenEffect.StartService(ServiceType.NOTIFICATION))
        }

        val hasOverlayPermission = Settings.canDrawOverlays(context)
        if (hasOverlayPermission) {
            sendEffect(MainScreenEffect.StartService(ServiceType.BUBBLE))
        }
    }

    private fun sendEffect(effectToSend: MainScreenEffect) = viewModelScope.launch {
        _effect.send(effectToSend)
    }

    fun loadStatistics() = viewModelScope.launch(ioDispatcher) {
        _statisticsState.update { it.copy(isLoading = true, error = null) }
        try {
            val total = wordRepository.getWordCount()
            val learned = wordRepository.getLearnedWordsCount()
            val percentage = if (total > 0) (learned * 100) / total else 0
            _statisticsState.update {
                it.copy(
                    totalWordsCount = total,
                    learnedPercentage = percentage,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _statisticsState.update {
                it.copy(
                    isLoading = false,
                    error = mapThrowableToUiError(e)
                )
            }
        }
    }

    fun reloadStatistics() {
        loadStatistics()
    }

    override fun onCleared() {
        super.onCleared()
        _effect.close()
    }
}