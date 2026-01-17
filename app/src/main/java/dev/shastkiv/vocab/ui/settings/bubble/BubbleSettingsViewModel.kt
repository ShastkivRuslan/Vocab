package dev.shastkiv.vocab.ui.settings.bubble

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import dev.shastkiv.vocab.domain.repository.ThemeRepository
import dev.shastkiv.vocab.service.bubble.BubbleService
import dev.shastkiv.vocab.ui.bubble.BubbleSettingsUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class BubbleSettingsViewModel @Inject constructor(
    private val repository: BubbleSettingsRepository,
    themeRepository: ThemeRepository,
    private val application: Application,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application) {

    private companion object {
        private const val MIN_BUBBLE_SIZE = 30f
        private const val MAX_BUBBLE_SIZE = 80f
        private const val MIN_TRANSPARENCY = 0f
        private const val MAX_TRANSPARENCY = 100f
    }

    private val _events = Channel<BubbleEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val repositorySettings = combine(
        repository.isBubbleEnabled,
        repository.bubbleSize,
        repository.bubbleTransparency,
        repository.isVibrationEnabled,
        repository.autoHideAppList
    ) { isEnabled, size, transparency, isVibrationOn, appList ->
        BubbleSettingsUiState(
            isBubbleEnabled = isEnabled,
            bubbleSize = size.coerceIn(MIN_BUBBLE_SIZE, MAX_BUBBLE_SIZE),
            bubbleTransparency = transparency.coerceIn(MIN_TRANSPARENCY, MAX_TRANSPARENCY),
            isVibrationEnabled = isVibrationOn,
            autoHideAppList = appList
        )
    }

    private var isWaitingForPermission = false
    private val _showDeniedSheet = MutableStateFlow(false)

    val uiState: StateFlow<BubbleSettingsUiState> = combine(
        repositorySettings,
        _showDeniedSheet
    ) { settings, showSheet ->
        settings.copy(
            hasOverlayPermission = Settings.canDrawOverlays(application),
            showDeniedSheet = showSheet
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BubbleSettingsUiState(hasOverlayPermission = Settings.canDrawOverlays(application))
    )

    val themeMode: StateFlow<Int> = themeRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    fun onBubbleEnabledChange(isEnabled: Boolean) = viewModelScope.launch(ioDispatcher) {
        if (isEnabled && !Settings.canDrawOverlays(application)) {
            _error.value = "PERMISSION_REQUIRED"
            return@launch
        }

        try {
            repository.setBubbleEnabled(isEnabled)
            if (isEnabled) startBubbleService() else stopBubbleService()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = application.getString(R.string.error_failed_to_update_bubble_settings)
        }
    }

    fun onBubbleSizeChange(size: Float) = viewModelScope.launch(ioDispatcher) {
        handleSettingChange {
            val constrainedSize = size.coerceIn(MIN_BUBBLE_SIZE, MAX_BUBBLE_SIZE)
            repository.setBubbleSize(constrainedSize)
        }
    }

    fun onTransparencyChange(transparency: Float) = viewModelScope.launch(ioDispatcher) {
        handleSettingChange {
            val constrainedTransparency = transparency.coerceIn(MIN_TRANSPARENCY, MAX_TRANSPARENCY)
            repository.setBubbleTransparency(constrainedTransparency)
        }
    }

    fun onVibrationEnabledChange(isEnabled: Boolean) = viewModelScope.launch(ioDispatcher) {
        handleSettingChange {
            repository.setVibrationEnabled(isEnabled)
        }
    }

    private suspend fun handleSettingChange(action: suspend () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _error.value = application.getString(R.string.error_failed_to_update_bubble_settings)
        }
    }

    private fun startBubbleService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, BubbleService::class.java)
        context.startForegroundService(serviceIntent)
    }

    private fun stopBubbleService() {
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, BubbleService::class.java)
        context.stopService(serviceIntent)
    }

    fun checkPermission() {
        val hasPermission = Settings.canDrawOverlays(application)

        viewModelScope.launch {
            if (hasPermission) {
                if (isWaitingForPermission) {
                    onBubbleEnabledChange(true)
                    _events.send(BubbleEvent.PermissionGrantedSuccess)
                }
                _showDeniedSheet.value = false
                isWaitingForPermission = false
            } else {
                if (isWaitingForPermission) {
                    _showDeniedSheet.value = true
                    isWaitingForPermission = false
                }
            }
        }
    }

    fun requestOverlayPermission() {
        isWaitingForPermission = true
        viewModelScope.launch { _events.send(BubbleEvent.OpenOverlaySettings) }
    }

    fun dismissDeniedSheet() {
        _showDeniedSheet.value = false
    }
}