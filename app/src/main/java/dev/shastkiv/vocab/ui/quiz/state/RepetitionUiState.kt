package dev.shastkiv.vocab.ui.quiz.state

import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.Word

sealed interface RepetitionUiState {
    object Loading : RepetitionUiState

    data class Content(
        val word: Word,
        val answerOptions: List<String>,
        val correctCount: Int,
        val wrongCount: Int,
        val selectedAnswerIndex: Int? = null,
        val isAnswerCorrect: Boolean? = null,
        val dailyStats: DailyStatistic?
    ) : RepetitionUiState

    data class Error(val error: UiError) : RepetitionUiState
}