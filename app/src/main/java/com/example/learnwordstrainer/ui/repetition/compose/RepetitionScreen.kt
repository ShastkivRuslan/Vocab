package com.example.learnwordstrainer.ui.repetition.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.ui.repetition.RepetitionEvent
import com.example.learnwordstrainer.ui.repetition.state.RepetitionUiState
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme

@Composable
fun RepetitionScreen(
    uiState: RepetitionUiState,
    onEvent: (RepetitionEvent) -> Unit,
    onBackPressed: () -> Unit
) {
    // TODO: Додати TopAppBar з кнопкою onBackPressed

    when (uiState) {
        is RepetitionUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is RepetitionUiState.Content -> {
            RepetitionContent(
                state = uiState,
                onAnswerClick = { index -> onEvent(RepetitionEvent.OnAnswerSelected(index)) },
                onNextWordClick = { onEvent(RepetitionEvent.OnNextWordClicked) },
                onListenClick = { onEvent(RepetitionEvent.OnListenClicked) }
            )
        }
        is RepetitionUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.message)
            }
        }
    }
}
@Preview(name = "Loading State", showBackground = true)
@Composable
fun RepetitionScreenPreview_Loading() {
    LearnWordsTrainerTheme {
        RepetitionScreen(
            uiState = RepetitionUiState.Loading,
            onEvent = {},
            onBackPressed = {}
        )
    }
}

@Preview(name = "Content State", showBackground = true)
@Composable
fun RepetitionScreenPreview_Content() {
    // Create sample data for the content state
    val sampleWord = Word(
        id = 1,
        englishWord = "Heuristic",
        translation = "Евристичний",
        correctAnswerCount = 12,
        wrongAnswerCount = 3
    )
    val sampleOptions = listOf("Евристичний", "Спорадичний", "Еклектичний", "Емпіричний")
    val sampleState = RepetitionUiState.Content(
        word = sampleWord,
        answerOptions = sampleOptions,
        correctCount = 12,
        wrongCount = 3
    )

    LearnWordsTrainerTheme {
        RepetitionScreen(
            uiState = sampleState,
            onEvent = {},
            onBackPressed = {}
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
fun RepetitionScreenPreview_Error() {
    LearnWordsTrainerTheme {
        RepetitionScreen(
            uiState = RepetitionUiState.Error("Failed to load words. Please try again."),
            onEvent = {},
            onBackPressed = {}
        )
    }
}
