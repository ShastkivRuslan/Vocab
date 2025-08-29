package com.example.learnwordstrainer.ui.mainscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.di.IoDispatcher
import com.example.learnwordstrainer.domain.model.Language
import com.example.learnwordstrainer.domain.model.LanguageSettings
import com.example.learnwordstrainer.domain.repository.LanguageRepository
import com.example.learnwordstrainer.domain.repository.SettingsRepository
import com.example.learnwordstrainer.domain.repository.ThemeRepository
import com.example.learnwordstrainer.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

enum class PermissionDialogType {
    NOTIFICATION, OVERLAY
}

enum class ServiceType {
    NOTIFICATION, BUBBLE
}

sealed interface MainScreenEvent {
    object OnCreate : MainScreenEvent
    object OnResume : MainScreenEvent
    data class OnPermissionDialogConfirm(val type: PermissionDialogType) : MainScreenEvent
    data class OnPermissionDialogDismiss(val type: PermissionDialogType, val wasDismissedForever: Boolean) : MainScreenEvent
    data class OnPermissionResult(val type: PermissionDialogType, val isGranted: Boolean) : MainScreenEvent
    data class OnDismissForeverChanged(val type: PermissionDialogType, val isChecked: Boolean): MainScreenEvent
}

sealed interface MainScreenEffect {
    data class RequestPermission(val type: PermissionDialogType) : MainScreenEffect
    data class StartService(val type: ServiceType) : MainScreenEffect
}

data class PermissionDialogState(
    val type: PermissionDialogType,
    val dismissForever: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    themeRepository: ThemeRepository,
    private val wordRepository: WordRepository,
    private val settingsRepository: SettingsRepository,
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
        val error: String? = null
    )

    private val _statisticsState = MutableStateFlow(StatisticsUiState())
    val statisticsState: StateFlow<StatisticsUiState> = _statisticsState.asStateFlow()

    private val _permissionDialogState = MutableStateFlow<PermissionDialogState?>(null)
    val permissionDialogState: StateFlow<PermissionDialogState?> = _permissionDialogState.asStateFlow()

    private val _effect = Channel<MainScreenEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadStatistics()
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnCreate -> checkPermissions()
            MainScreenEvent.OnResume -> loadStatistics()
            is MainScreenEvent.OnPermissionDialogConfirm -> {
                _permissionDialogState.value = null
                sendEffect(MainScreenEffect.RequestPermission(event.type))
            }
            is MainScreenEvent.OnPermissionDialogDismiss -> {
                _permissionDialogState.value = null
                handleDialogDismiss(event.type, event.wasDismissedForever)
            }
            is MainScreenEvent.OnPermissionResult -> handlePermissionResult(event.type, event.isGranted)
            is MainScreenEvent.OnDismissForeverChanged -> {
                _permissionDialogState.update { it?.copy(dismissForever = event.isChecked) }
            }
        }
    }

    private fun checkPermissions() = viewModelScope.launch(ioDispatcher) {
        try {
            checkNotificationPermission()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _statisticsState.update { it.copy(error = context.getString(R.string.error_permission_check_failed)) }
        }
    }

    private suspend fun checkNotificationPermission() {
        val isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (isPermissionGranted) {
            sendEffect(MainScreenEffect.StartService(ServiceType.NOTIFICATION))
            checkOverlayPermission()
        } else {
            val isDismissedForever = settingsRepository.hasDismissedNotificationPermission.first()
            if (!isDismissedForever) {
                _permissionDialogState.value = PermissionDialogState(PermissionDialogType.NOTIFICATION)
            } else {
                checkOverlayPermission()
            }
        }
    }

    private suspend fun checkOverlayPermission() {
        val isPermissionGranted = Settings.canDrawOverlays(context)
        if (isPermissionGranted) {
            sendEffect(MainScreenEffect.StartService(ServiceType.BUBBLE))
        } else {
            val isDismissedForever = settingsRepository.hasDismissedOverlayPermission.first()
            if (!isDismissedForever) {
                _permissionDialogState.value = PermissionDialogState(PermissionDialogType.OVERLAY)
            }
        }
    }

    private fun handlePermissionResult(type: PermissionDialogType, isGranted: Boolean) = viewModelScope.launch(ioDispatcher) {
        try {
            when (type) {
                PermissionDialogType.NOTIFICATION -> {
                    if (isGranted) {
                        sendEffect(MainScreenEffect.StartService(ServiceType.NOTIFICATION))
                    }
                    checkOverlayPermission()
                }
                PermissionDialogType.OVERLAY -> {
                    if (isGranted) {
                        sendEffect(MainScreenEffect.StartService(ServiceType.BUBBLE))
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _statisticsState.update { it.copy(error = context.getString(R.string.error_permission_handling_failed)) }
        }
    }

    private fun handleDialogDismiss(type: PermissionDialogType, shouldDismissForever: Boolean) = viewModelScope.launch(ioDispatcher) {
        try {
            if (shouldDismissForever) {
                when (type) {
                    PermissionDialogType.NOTIFICATION -> settingsRepository.setNotificationPermissionDismissed(true)
                    PermissionDialogType.OVERLAY -> settingsRepository.setOverlayPermissionDismissed(true)
                }
            }
            if (type == PermissionDialogType.NOTIFICATION) {
                checkOverlayPermission()
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _statisticsState.update { it.copy(error = context.getString(R.string.error_failed_to_save_settings)) }
        }
    }

    private fun sendEffect(effectToSend: MainScreenEffect) = viewModelScope.launch {
        _effect.send(effectToSend)
    }

    private fun loadStatistics() = viewModelScope.launch(ioDispatcher) {
        _statisticsState.update { it.copy(isLoading = true, error = null) }
        try {
            val total = wordRepository.getWordCount()
            val learned = wordRepository.getLearnedWordsCount()
            val percentage = if (total > 0) (learned * 100) / total else 0
            _statisticsState.update {
                it.copy(totalWordsCount = total, learnedPercentage = percentage, isLoading = false)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _statisticsState.update {
                it.copy(isLoading = false, error = context.getString(R.string.error_failed_to_load_statistics))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _effect.close()
    }
}