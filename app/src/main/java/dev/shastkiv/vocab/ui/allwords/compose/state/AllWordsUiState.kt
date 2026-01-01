package dev.shastkiv.vocab.ui.allwords.compose.state

import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.Word

sealed interface AllWordsUiState {
    data object Loading : AllWordsUiState
    data class Success(val words: List<Word>) : AllWordsUiState
    data class Error(val error: UiError) : AllWordsUiState
}