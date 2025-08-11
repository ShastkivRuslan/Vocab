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
import com.example.learnwordstrainer.domain.repository.SettingsRepository
import com.example.learnwordstrainer.domain.repository.ThemeRepository
import com.example.learnwordstrainer.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    object OnSettingsClick : MainScreenEvent
    object OnAddWordClick : MainScreenEvent
    object OnRepetitionClick : MainScreenEvent
    object OnAllWordsClick : MainScreenEvent
    object OnPracticeClick : MainScreenEvent
    data class OnPermissionDialogConfirm(val type: PermissionDialogType) : MainScreenEvent
    data class OnPermissionDialogDismiss(val type: PermissionDialogType, val shouldDismissForever: Boolean) : MainScreenEvent
    data class OnPermissionResult(val type: PermissionDialogType, val isGranted: Boolean) : MainScreenEvent
}

sealed interface MainScreenEffect {
    data class Navigate(val route: String) : MainScreenEffect
    data class RequestPermission(val type: PermissionDialogType) : MainScreenEffect
    data class ShowPermissionDialog(val type: PermissionDialogType) : MainScreenEffect
    data class StartService(val type: ServiceType) : MainScreenEffect
}

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    themeRepository: ThemeRepository,
    private val wordRepository: WordRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<Int> = themeRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    val isDismissedNotificationPermission: StateFlow<Boolean> =
        settingsRepository.hasDismissedNotificationPermission
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    val isDismissedOverlayPermission: StateFlow<Boolean> =
        settingsRepository.hasDismissedOverlayPermission
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    data class StatisticsUiState(
        val totalWordsCount: Int = 0,
        val learnedPercentage: Int = 0
    )

    private val _statisticsState = MutableStateFlow(StatisticsUiState())
    val statisticsState: StateFlow<StatisticsUiState> = _statisticsState.asStateFlow()

    private val _effect = Channel<MainScreenEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadStatistics()
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnCreate -> checkPermissions()
            MainScreenEvent.OnResume -> loadStatistics()
            is MainScreenEvent.OnPermissionDialogConfirm -> sendEffect(MainScreenEffect.RequestPermission(event.type))
            is MainScreenEvent.OnPermissionDialogDismiss -> handleDialogDismiss(event.type, event.shouldDismissForever)
            is MainScreenEvent.OnPermissionResult -> handlePermissionResult(event.type, event.isGranted)

            MainScreenEvent.OnSettingsClick -> navigate("settings")
            MainScreenEvent.OnAddWordClick -> navigate("add_word")
            MainScreenEvent.OnRepetitionClick -> navigate("repetition")
            MainScreenEvent.OnAllWordsClick -> navigate("all_words")
            MainScreenEvent.OnPracticeClick -> navigate("practice")
        }
    }

    private fun checkPermissions() {
        viewModelScope.launch {
            checkNotificationPermission()
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
                sendEffect(MainScreenEffect.ShowPermissionDialog(PermissionDialogType.NOTIFICATION))
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
                sendEffect(MainScreenEffect.ShowPermissionDialog(PermissionDialogType.OVERLAY))
            }
        }
    }

    private fun handlePermissionResult(type: PermissionDialogType, isGranted: Boolean) {
        viewModelScope.launch {
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
        }
    }

    private fun handleDialogDismiss(type: PermissionDialogType, shouldDismissForever: Boolean) {
        viewModelScope.launch {
            if (shouldDismissForever) {
                when (type) {
                    PermissionDialogType.NOTIFICATION ->
                        settingsRepository.setNotificationPermissionDismissed(true)
                    PermissionDialogType.OVERLAY ->
                        settingsRepository.setOverlayPermissionDismissed(true)
                }
            }

            when (type) {
                PermissionDialogType.NOTIFICATION -> {
                    checkOverlayPermission()
                }
                PermissionDialogType.OVERLAY -> {

                }
            }
        }
    }

    private fun navigate(route: String) = sendEffect(MainScreenEffect.Navigate(route))
    private fun sendEffect(effectToSend: MainScreenEffect) = viewModelScope.launch { _effect.send(effectToSend) }

    private fun loadStatistics() {
        viewModelScope.launch {
            val total = wordRepository.getWordCount()
            val learned = wordRepository.getLearnedWordsCount()
            val percentage = if (total > 0) (learned * 100) / total else 0
            _statisticsState.update { it.copy(totalWordsCount = total, learnedPercentage = percentage) }
        }
    }
}