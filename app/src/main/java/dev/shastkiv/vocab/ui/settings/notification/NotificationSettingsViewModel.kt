package dev.shastkiv.vocab.ui.settings.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.enums.WordType
import dev.shastkiv.vocab.service.notification.NotificationStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationStateManager: NotificationStateManager
) : ViewModel() {

    fun callEcho() {
        viewModelScope.launch {
            notificationStateManager.updateToWordEchoState(
                Word(
                    id = 1,
                    sourceWord = "Apple",
                    translation = "Яблуко",
                    sourceLanguageCode = "en",
                    targetLanguageCode = "uk",
                    wordType = WordType.FREE,
                    isWordAdded = true
                ), 3
            )
        }
    }

    fun callGentle() {
        viewModelScope.launch {
            notificationStateManager.updateToGentleNudgeState(10)
        }
    }

    fun callSuccess() {
        viewModelScope.launch {
            notificationStateManager.updateToSuccessMomentState("Banana")
        }
    }

    fun callStreak() {
        viewModelScope.launch {
            notificationStateManager.updateToStreakKeeperState(4)
        }
    }
}