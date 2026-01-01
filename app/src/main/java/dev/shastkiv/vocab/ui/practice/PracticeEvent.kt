package dev.shastkiv.vocab.ui.practice

sealed interface PracticeEvent {
    // Користувач натиснув "Наступне слово"
    object OnNextWordClicked : PracticeEvent

    // Користувач хоче прослухати слово
    object OnListenClicked : PracticeEvent

    // TODO: В майбутньому для преміум-чату
    // data class OnAskAiClicked(val question: String) : PracticeEvent
}