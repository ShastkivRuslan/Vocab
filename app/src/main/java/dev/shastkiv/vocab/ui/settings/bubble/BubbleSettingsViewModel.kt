package dev.shastkiv.vocab.ui.settings.bubble

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import dev.shastkiv.vocab.domain.repository.ThemeRepository
import dev.shastkiv.vocab.service.bubble.BubbleService
import dev.shastkiv.vocab.ui.bubble.BubbleSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val uiState: StateFlow<BubbleSettingsUiState> = combine(
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BubbleSettingsUiState()
    )

    val themeMode: StateFlow<Int> = themeRepository.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )

    fun onBubbleEnabledChange(isEnabled: Boolean) = viewModelScope.launch(ioDispatcher) {
        //TODO: implement overlay permission request if not granted and user want turn on vocab+
        try {
            repository.setBubbleEnabled(isEnabled)
            if (isEnabled) {
                startBubbleService()
            } else {
                stopBubbleService()
            }
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
}