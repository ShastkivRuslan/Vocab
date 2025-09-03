package com.example.learnwordstrainer.ui.practice.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.ui.practice.PracticeEvent
import com.example.learnwordstrainer.ui.practice.PracticeUiState
import com.example.learnwordstrainer.ui.practice.compose.components.AiChatView
import com.example.learnwordstrainer.ui.practice.compose.components.PracticeFooter
import com.example.learnwordstrainer.ui.practice.compose.components.WordCardDeck

@Composable
fun PracticeContent(
    state: PracticeUiState.Content,
    onEvent: (PracticeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val fakeWord1 = Word(id = 1, sourceWord = "Ephemeral22", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1")
        val fakeWord = Word(id = 1, sourceWord = "Ephemeral", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1")
        // Анімована колода карток зі словом
        WordCardDeck(
            currentWord = state.currentWord,
            onListenClick = { onEvent(PracticeEvent.OnListenClicked) },
            nextWordsInDeck = listOf(fakeWord,fakeWord1)
        )

        // "Чат" з прикладами від AI
        AiChatView(
            examples = state.examples,
            isLoading = state.isAiLoadingExamples
        )
    }

    // Кнопка "Наступне слово" внизу екрана
    PracticeFooter(
        onNextWordClick = { onEvent(PracticeEvent.OnNextWordClicked) }
    )
}