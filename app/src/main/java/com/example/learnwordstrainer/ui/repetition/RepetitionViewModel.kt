package com.example.learnwordstrainer.ui.repetition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnwordstrainer.domain.usecase.GetRepetitionWordUseCase
import com.example.learnwordstrainer.domain.usecase.UpdateWordScoreUseCase
import com.example.learnwordstrainer.ui.repetition.state.RepetitionUiState
import com.example.learnwordstrainer.utils.TTSManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepetitionViewModel @Inject constructor(
    private val getRepetitionWordUseCase: GetRepetitionWordUseCase,
    private val updateWordScoreUseCase: UpdateWordScoreUseCase,
    private val ttsManager: TTSManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepetitionUiState>(RepetitionUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }

    fun onEvent(event: RepetitionEvent) {
        when (event) {
            is RepetitionEvent.OnAnswerSelected -> handleAnswerSelection(event.index)
            is RepetitionEvent.OnNextWordClicked -> loadNextWord()
            is RepetitionEvent.OnListenClicked -> {
                val currentState = _uiState.value
                if (currentState is RepetitionUiState.Content) {
                    ttsManager.speak(currentState.word.englishWord)
                }
            }
        }
    }

    private fun handleAnswerSelection(selectedIndex: Int) {
        val currentState = _uiState.value
        if (currentState is RepetitionUiState.Content && currentState.selectedAnswerIndex == null) {
            val selectedOption = currentState.answerOptions[selectedIndex]
            val isCorrect = selectedOption == currentState.word.translation

            viewModelScope.launch {
                updateWordScoreUseCase(
                    wordId = currentState.word.id,
                    currentCorrectCount = currentState.word.correctAnswerCount,
                    currentWrongCount = currentState.word.wrongAnswerCount,
                    wasAnswerCorrect = isCorrect
                )
            }

            _uiState.update {
                (it as RepetitionUiState.Content).copy(
                    selectedAnswerIndex = selectedIndex,
                    isAnswerCorrect = isCorrect
                )
            }
        }
    }

    private fun loadNextWord() {
        viewModelScope.launch {
            _uiState.value = RepetitionUiState.Loading
            val repetitionData = getRepetitionWordUseCase()

            if (repetitionData != null) {
                _uiState.value = RepetitionUiState.Content(
                    word = repetitionData.word,
                    answerOptions = repetitionData.options,
                    correctCount = repetitionData.word.correctAnswerCount,
                    wrongCount = repetitionData.word.wrongAnswerCount
                )
            } else {
                _uiState.value = RepetitionUiState.Error("Слова для повторення закінчились.")
            }
        }
    }
}

sealed interface RepetitionEvent {
    data class OnAnswerSelected(val index: Int) : RepetitionEvent
    object OnNextWordClicked : RepetitionEvent
    object OnListenClicked : RepetitionEvent
}