package dev.shastkiv.vocab.ui.flashcard.state

import dev.shastkiv.vocab.domain.model.CategoryCounts
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.enums.WordCategory
import dev.shastkiv.vocab.ui.quiz.compose.components.SegmentState

sealed interface FlashCardUiState {
    object Loading : FlashCardUiState

    data class Lobby(
        val dailyStats: DailyStatistic?,
        val wordsCategorySelected: WordCategory,
        val counts: CategoryCounts
    ) : FlashCardUiState

    data class Content(
        val word: Word,
        val nextWords: List<Word> = emptyList(),   // наступні слова — для стеку карток
        val currentStep: Int,
        val totalSteps: Int,
        val segmentStates: List<SegmentState>,
        val isAnswered: Boolean = false,
        val isAnswerCorrect: Boolean? = null,
        val dailyStats: DailyStatistic? = null
    ) : FlashCardUiState

    data class SessionFinished(
        val totalCorrect: Int,
        val totalWrong: Int,
        val masteryEarned: Int
    ) : FlashCardUiState

    data class Error(val error: UiError) : FlashCardUiState
}