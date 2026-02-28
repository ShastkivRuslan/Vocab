package dev.shastkiv.vocab.ui.settings.widget

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shastkiv.vocab.domain.model.WidgetClickAction
import dev.shastkiv.vocab.domain.model.WidgetFilterMode
import dev.shastkiv.vocab.domain.repository.WidgetSettingsRepository
import dev.shastkiv.vocab.domain.usecase.ScheduleWidgetUpdatesUseCase
import dev.shastkiv.vocab.utils.WidgetHelper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetSettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: WidgetSettingsRepository,
    private val scheduleWidgetUpdatesUseCase: ScheduleWidgetUpdatesUseCase
) : ViewModel() {

    private val _isWidgetAdded = MutableStateFlow(true)

    private val _events = Channel<WidgetSettingsEvent>()
    val events = _events.receiveAsFlow()

    val uiState = combine(_isWidgetAdded, repository.widgetSettings) { isAdded, settings ->
        WidgetSettingsUiState.Success(
            isWidgetAdded = isAdded,
            settings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WidgetSettingsUiState.Loading
    )

    fun checkWidgetStatus() {
        viewModelScope.launch {
            _isWidgetAdded.value = WidgetHelper.isWidgetInstalled(context)
        }
    }

    fun onAddWidgetClick() {
        viewModelScope.launch {
            _events.send(WidgetSettingsEvent.RequestPinWidget)
        }
    }

    fun updateUpdateFrequency(minutes: Int) {
        viewModelScope.launch {
            repository.updateUpdateFrequency(minutes)
            // Перепланувати оновлення з новим інтервалом
            rescheduleWidgetUpdates()
        }
    }

    fun updateShowTranslation(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowTranslation(show)
            // Миттєво оновити віджет, щоб показати зміни
            triggerImmediateWidgetUpdate()
        }
    }

    fun updateFilterMode(mode: WidgetFilterMode) {
        viewModelScope.launch {
            repository.updateFilterMode(mode)
            // Миттєво оновити віджет з новим словом згідно фільтру
            triggerImmediateWidgetUpdate()
        }
    }

    fun updateClickAction(action: WidgetClickAction) {
        viewModelScope.launch {
            repository.updateClickAction(action)
        }
    }

    /**
     * Перепланувати періодичні оновлення віджета
     */
    private suspend fun rescheduleWidgetUpdates() {
        try {
            scheduleWidgetUpdatesUseCase()
        } catch (e: Exception) {
            android.util.Log.e("WidgetSettingsVM", "Error rescheduling updates", e)
        }
    }

    /**
     * Миттєво оновити віджет для відображення змін
     */
    private fun triggerImmediateWidgetUpdate() {
        viewModelScope.launch {
            try {
                androidx.work.WorkManager.getInstance(context).enqueue(
                    androidx.work.OneTimeWorkRequestBuilder<dev.shastkiv.vocab.ui.widget.UpdateWidgetWorker>()
                        .build()
                )
            } catch (e: Exception) {
                android.util.Log.e("WidgetSettingsVM", "Error triggering update", e)
            }
        }
    }
}