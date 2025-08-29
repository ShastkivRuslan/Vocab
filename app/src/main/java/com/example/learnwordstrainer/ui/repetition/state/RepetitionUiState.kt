package com.example.learnwordstrainer.ui.repetition.state

import com.example.learnwordstrainer.domain.model.DailyStatistic
import com.example.learnwordstrainer.domain.model.Word

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

    data class Error(val message: String) : RepetitionUiState
}