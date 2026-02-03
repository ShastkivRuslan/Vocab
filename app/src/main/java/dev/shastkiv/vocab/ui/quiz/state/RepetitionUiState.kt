package dev.shastkiv.vocab.ui.quiz.state

import dev.shastkiv.vocab.domain.model.CategoryCounts
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.enums.WordCategory
import dev.shastkiv.vocab.ui.quiz.compose.components.SegmentState

sealed interface RepetitionUiState {
    object Loading : RepetitionUiState

    data class Lobby(
        val dailyStats: DailyStatistic?,
        val wordsCategorySelected: WordCategory,
        val counts: CategoryCounts
    ) : RepetitionUiState

    data class Content(
        val word: Word,
        val answerOptions: List<String>,
        val currentStep: Int,
        val totalSteps: Int,
        val segmentStates: List<SegmentState>,
        val selectedAnswerIndex: Int? = null,
        val isAnswerCorrect: Boolean? = null,
        val dailyStats: DailyStatistic?
    ) : RepetitionUiState

    data class SessionFinished(
        val totalCorrect: Int,
        val totalWrong: Int,
        val masteryEarned: Int,
        val hasWrongAnswers: Boolean = false
    ) : RepetitionUiState

    data class Error(val error: UiError) : RepetitionUiState
}