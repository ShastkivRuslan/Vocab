package com.shastkiv.vocab.ui.repetition.state

import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.model.UiError
import com.shastkiv.vocab.domain.model.Word

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