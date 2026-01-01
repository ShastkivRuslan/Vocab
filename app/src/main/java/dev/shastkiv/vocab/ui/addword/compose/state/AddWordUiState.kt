package dev.shastkiv.vocab.ui.addword.compose.state

import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.WordData

sealed interface UserStatus {
    object Free : UserStatus
    object Premium : UserStatus
}

sealed interface AddWordUiState {
    data class Idle(val userStatus: UserStatus) : AddWordUiState
    object Loading : AddWordUiState

    data class Success(
        val userStatus: UserStatus,
        val originalWord: String,
        val wordData: WordData?,
        val simpleTranslation: String?,
        val isAlreadySaved: Boolean,
        val isMainSectionExpanded: Boolean = false,
        val isExamplesSectionExpanded: Boolean = false,
        val isUsageInfoSectionExpanded: Boolean = false
    ) : AddWordUiState

    data class SavingWord(
        val word: WordData,
        val isAlreadySaved: Boolean,
        val savingStep: SavingStep,
        val isMainSectionExpanded: Boolean = false,
        val isExamplesSectionExpanded: Boolean = false,
        val isUsageInfoSectionExpanded: Boolean = false
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
    object ShowPaywall : AddWordUiState
    object DialogShouldClose : AddWordUiState
}