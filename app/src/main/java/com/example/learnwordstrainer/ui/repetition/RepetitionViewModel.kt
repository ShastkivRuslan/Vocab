package com.example.learnwordstrainer.ui.repetition

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.di.IoDispatcher
import com.example.learnwordstrainer.domain.model.DailyStatistic
import com.example.learnwordstrainer.domain.repository.ThemeRepository
import com.example.learnwordstrainer.domain.usecase.GetRepetitionWordUseCase
import com.example.learnwordstrainer.domain.usecase.GetTodayStatsUseCase
import com.example.learnwordstrainer.domain.usecase.StatType
import com.example.learnwordstrainer.domain.usecase.UpdateDailyStatsUseCase
import com.example.learnwordstrainer.domain.usecase.UpdateWordScoreUseCase
import com.example.learnwordstrainer.ui.repetition.state.RepetitionUiState
import com.example.learnwordstrainer.utils.TTSManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class RepetitionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    themeRepository: ThemeRepository,
    private val getRepetitionWordUseCase: GetRepetitionWordUseCase,
    private val updateWordScoreUseCase: UpdateWordScoreUseCase,
    private val updateDailyStatsUseCase: UpdateDailyStatsUseCase,
    private val ttsManager: TTSManager,
    getTodayStatsUseCase: GetTodayStatsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val currentTheme: StateFlow<Int> = themeRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )

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
                    _uiState.value = RepetitionUiState.Error(context.getString(R.string.error_failed_to_save_answer))
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
                // Виклик юзкейса залишається тим самим, але тепер він враховує мову
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
                    // ЗМІНА: Показуємо більш конкретну помилку, якщо для обраної мови немає слів
                    _uiState.value = RepetitionUiState.Error(context.getString(R.string.error_no_words_for_language))
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiState.value = RepetitionUiState.Error(context.getString(R.string.error_failed_to_load_word))
            }
        }
    }
}

sealed interface RepetitionEvent {
    data class OnAnswerSelected(val index: Int) : RepetitionEvent
    object OnNextWordClicked : RepetitionEvent
    object OnListenClicked : RepetitionEvent
}