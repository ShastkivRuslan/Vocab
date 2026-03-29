package dev.shastkiv.vocab.ui.flashcard

import dev.shastkiv.vocab.domain.model.enums.WordCategory

sealed interface FlashCardEvent {
    data class OnCategoryChanged(val category: WordCategory) : FlashCardEvent
    object OnStartSessionClicked : FlashCardEvent
    object OnKnowClicked : FlashCardEvent
    object OnDontKnowClicked : FlashCardEvent
    object OnNextCardClicked : FlashCardEvent
    object OnListenClicked : FlashCardEvent
    object OnReturnToLobby : FlashCardEvent
}