package com.shastkiv.vocab.ui.repetition

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shastkiv.vocab.di.IoDispatcher
import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.model.enums.StatType
import com.shastkiv.vocab.domain.model.UiError
import com.shastkiv.vocab.domain.repository.ThemeRepository
import com.shastkiv.vocab.domain.usecase.GetRepetitionWordUseCase
import com.shastkiv.vocab.domain.usecase.GetTodayStatsUseCase
import com.shastkiv.vocab.domain.usecase.UpdateDailyStatsUseCase
import com.shastkiv.vocab.domain.usecase.UpdateWordScoreUseCase
import com.shastkiv.vocab.ui.repetition.state.RepetitionUiState
import com.shastkiv.vocab.utils.TTSManager
import com.shastkiv.vocab.utils.mapThrowableToUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class RepetitionViewModel @Inject constructor(
    private val getRepetitionWordUseCase: GetRepetitionWordUseCase,
    private val updateWordScoreUseCase: UpdateWordScoreUseCase,
    private val updateDailyStatsUseCase: UpdateDailyStatsUseCase,
    private val ttsManager: TTSManager,
    getTodayStatsUseCase: GetTodayStatsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val dailyStatisticFlow: Flow<DailyStatistic?> = getTodayStatsUseCase()

    private val _uiState = MutableStateFlow<RepetitionUiState>(RepetitionUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var isProcessingAnswer = false

    init {
        loadNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }

    fun onEvent(event: RepetitionEvent) {
        when (event) {
            is RepetitionEvent.OnAnswerSelected -> {
                if (!isProcessingAnswer) {
                    handleAnswerSelection(event.index)
                }
            }
            is RepetitionEvent.OnNextWordClicked -> loadNextWord()
            is RepetitionEvent.OnListenClicked -> handleListen()
        }
    }

    private fun handleListen() {
        val currentState = _uiState.value
        if (currentState is RepetitionUiState.Content) {
            ttsManager.speak(currentState.word.sourceWord)
        }
    }

    private fun handleAnswerSelection(selectedIndex: Int) {
        val currentState = _uiState.value
        if (currentState is RepetitionUiState.Content &&
            currentState.selectedAnswerIndex == null &&
            selectedIndex < currentState.answerOptions.size) {

            isProcessingAnswer = true
            val selectedOption = currentState.answerOptions[selectedIndex]
            val isCorrect = selectedOption == currentState.word.translation

            viewModelScope.launch(ioDispatcher) {
                try {
                    updateWordScoreUseCase(
                        wordId = currentState.word.id,
                        currentCorrectCount = currentState.word.correctAnswerCount,
                        currentWrongCount = currentState.word.wrongAnswerCount,
                        wasAnswerCorrect = isCorrect
                    )
                    val statType = if (isCorrect) StatType.CORRECT_ANSWER else StatType.WRONG_ANSWER
                    updateDailyStatsUseCase(statType)

                    _uiState.update {
                        (it as RepetitionUiState.Content).copy(
                            selectedAnswerIndex = selectedIndex,
                            isAnswerCorrect = isCorrect
                        )
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    _uiState.value = RepetitionUiState.Error(mapThrowableToUiError(e))
                } finally {
                    isProcessingAnswer = false
                }
            }
        }
    }

    private fun loadNextWord() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = RepetitionUiState.Loading
            try {
                val repetitionData = getRepetitionWordUseCase()
                if (repetitionData != null) {
                    val currentStats = dailyStatisticFlow.firstOrNull()
                    _uiState.value = RepetitionUiState.Content(
                        word = repetitionData.word,
                        answerOptions = repetitionData.options,
                        correctCount = repetitionData.word.correctAnswerCount,
                        wrongCount = repetitionData.word.wrongAnswerCount,
                        dailyStats = currentStats
                    )
                } else {
                    _uiState.value = RepetitionUiState.Error(UiError.EmptyData)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.value = RepetitionUiState.Error(mapThrowableToUiError(e))
            }
        }
    }
}

sealed interface RepetitionEvent {
    data class OnAnswerSelected(val index: Int) : RepetitionEvent
    object OnNextWordClicked : RepetitionEvent
    object OnListenClicked : RepetitionEvent
}