package com.shastkiv.vocab.ui.practice

import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.ExampleData

sealed interface PracticeUiState {
    object Loading : PracticeUiState

    data class Content(
        val currentWord: Word,
        val remainingWordsCount: Int = 0,
        val examples: List<ExampleData> = emptyList(),
        val isAiLoadingExamples: Boolean = false
    ) : PracticeUiState

    data class Empty(val message: String) : PracticeUiState

    data class Error(val message: String) : PracticeUiState
}