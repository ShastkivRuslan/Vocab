package dev.shastkiv.vocab.ui.mainscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.model.LanguageSettings
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.repository.BubbleSettingsRepository
import dev.shastkiv.vocab.domain.repository.LanguageRepository
import dev.shastkiv.vocab.domain.repository.ThemeRepository
import dev.shastkiv.vocab.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.math.roundToInt

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
    private val bubbleSettingsRepository: BubbleSettingsRepository,
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
        val notLearnedWordsCount: Int = 0,
        val todayWordsCount: Int = 0,
        val learnedPercentage: Int = 0,
        val isLoading: Boolean = true,
        val error: UiError? = null
    )

    private val _effect = Channel<MainScreenEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        startServicesIfPermissionsGranted()
    }

    fun onResume() {
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
        val isBubbleEnabled = bubbleSettingsRepository.isBubbleEnabled.first()
        if (hasOverlayPermission && isBubbleEnabled) {
            sendEffect(MainScreenEffect.StartService(ServiceType.BUBBLE))
        }
    }

    private fun sendEffect(effectToSend: MainScreenEffect) = viewModelScope.launch {
        _effect.send(effectToSend)
    }

    private fun calculateWordsForTodayFlow(): Flow<Int> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val startOfTodayZoned = now
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())

        val startDate: LocalDateTime = startOfTodayZoned.toLocalDateTime()
        val endDate: LocalDateTime = now.toLocalDateTime()

        return wordRepository.getWordsAddedBetween(startDate, endDate)
            .map { it.size }
    }

    val statisticsState: StateFlow<StatisticsUiState> = combine(
        wordRepository.getWordCount(),
        wordRepository.getLearnedWordsCount(),
        wordRepository.getWordsNeedingRepetition(),
        calculateWordsForTodayFlow()
    ) { totalWords, learnedWords, notLearnedWords, todayWordsCount ->

        val percentage: Int = if (totalWords > 0) {
            ((learnedWords.toDouble() / totalWords) * 100).roundToInt()
        } else {
            0
        }

        StatisticsUiState(
            totalWordsCount = totalWords,
            learnedPercentage = percentage,
            todayWordsCount = todayWordsCount,
            notLearnedWordsCount = notLearnedWords,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState(isLoading = true)
    )

    override fun onCleared() {
        super.onCleared()
        _effect.close()
    }
}