package dev.shastkiv.vocab.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shastkiv.vocab.di.IoDispatcher
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.enums.StatType
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.usecase.GetRepetitionWordUseCase
import dev.shastkiv.vocab.domain.usecase.GetTodayStatsUseCase
import dev.shastkiv.vocab.domain.usecase.UpdateDailyStatsUseCase
import dev.shastkiv.vocab.domain.usecase.UpdateWordScoreUseCase
import dev.shastkiv.vocab.ui.quiz.state.RepetitionUiState
import dev.shastkiv.vocab.utils.TTSManager
import dev.shastkiv.vocab.utils.mapThrowableToUiError
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val dailyStats: StateFlow<DailyStatistic?> = dailyStatisticFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

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
        val currentState = _uiState.value as? RepetitionUiState.Content ?: return

        if (currentState.selectedAnswerIndex != null || selectedIndex >= currentState.answerOptions.size) return

        isProcessingAnswer = true
        val selectedOption = currentState.answerOptions[selectedIndex]
        val isCorrect = selectedOption == currentState.word.translation

        val updatedState = currentState.copy(
            selectedAnswerIndex = selectedIndex,
            isAnswerCorrect = isCorrect,
            correctCount = if (isCorrect) currentState.correctCount + 1 else currentState.correctCount,
            wrongCount = if (!isCorrect) currentState.wrongCount + 1 else currentState.wrongCount,
            dailyStats = currentState.dailyStats?.let { stats ->
                stats.copy(
                    correctAnswers = if (isCorrect) stats.correctAnswers + 1 else stats.correctAnswers,
                    wrongAnswers = if (!isCorrect) stats.wrongAnswers + 1 else stats.wrongAnswers
                )
            }
        )
        _uiState.value = updatedState

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
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.value = RepetitionUiState.Error(mapThrowableToUiError(e))
            } finally {
                isProcessingAnswer = false
            }
        }
    }

    private fun loadNextWord() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = RepetitionUiState.Loading
            try {
                val repetitionData = getRepetitionWordUseCase()
                //TODO:implement load quiz words based on selected target language from settings
                if (repetitionData != null) {
                    val currentStats = dailyStats.value
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