package com.example.learnwordstrainer.ui.addwordfloating.compose.state

import com.example.learnwordstrainer.domain.model.WordData

sealed interface AddWordUiState {
    object Idle : AddWordUiState
    object Loading : AddWordUiState

    data class Success(
        val word: WordData,
        val isAlreadySaved: Boolean,
        val isMainSectionExpanded: Boolean = false,
        val isExamplesSectionExpanded: Boolean = false,
        val isContextSectionExpanded: Boolean = false
    ) : AddWordUiState

    data class SavingWord(
        val word: WordData,
        val isAlreadySaved: Boolean,
        val savingStep: SavingStep,
        val isMainSectionExpanded: Boolean = false,
        val isExamplesSectionExpanded: Boolean = false,
        val isContextSectionExpanded: Boolean = false
    ) : AddWordUiState {
        val shouldShowSections: Boolean = savingStep == SavingStep.CollapsingCards
        val shouldShowLoader: Boolean = savingStep == SavingStep.Saving
        val shouldShowSuccess: Boolean = savingStep == SavingStep.Success
    }

    enum class SavingStep {
        CollapsingCards,
        Saving,
        Success
    }

    data class Error(val type: UiError) : AddWordUiState
    object DialogShouldClose : AddWordUiState
}