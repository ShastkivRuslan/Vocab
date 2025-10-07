package com.shastkiv.vocab.ui.repetition.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.DailyStatistic
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.ui.common.compose.ErrorContent
import com.shastkiv.vocab.ui.repetition.RepetitionEvent
import com.shastkiv.vocab.ui.repetition.state.RepetitionUiState
import com.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme

@Composable
fun RepetitionScreen(
    uiState: RepetitionUiState,
    onEvent: (RepetitionEvent) -> Unit,
    onBackPressed: () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed, modifier = Modifier.size(48.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_left_chevron),
                    contentDescription = stringResource(R.string.back_button_description),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = stringResource(R.string.repeat_mode),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (uiState) {
                is RepetitionUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RepetitionUiState.Content -> {
                    RepetitionContent(
                        state = uiState,
                        dailyCorrectCount = uiState.dailyStats?.correctAnswers ?: 0,
                        dailyWrongCount = uiState.dailyStats?.wrongAnswers ?: 0,
                        onAnswerClick = { index -> onEvent(RepetitionEvent.OnAnswerSelected(index)) },
                        onNextWordClick = { onEvent(RepetitionEvent.OnNextWordClicked) },
                        onListenClick = { onEvent(RepetitionEvent.OnListenClicked) }
                    )
                }
                is RepetitionUiState.Error -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = { onEvent(RepetitionEvent.OnNextWordClicked) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
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
    val sampleWord = Word(
        id = 1,
        sourceWord = "Heuristic",
        translation = "Евристичний",
        correctAnswerCount = 12,
        wrongAnswerCount = 3,
        sourceLanguageCode = "en",
        targetLanguageCode = "uk"
    )
    val sampleOptions = listOf("Евристичний", "Спорадичний", "Еклектичний", "Емпіричний")
    val sampleStats = DailyStatistic(
        date = "2025-08-15",
        correctAnswers = 87,
        wrongAnswers = 15,
        wordsAdded = 4,
        wordsAsked = 11
    )
    val sampleState = RepetitionUiState.Content(
        word = sampleWord,
        answerOptions = sampleOptions,
        dailyStats = sampleStats,
        correctCount = 10,
        wrongCount = 20
    )

    LearnWordsTrainerTheme {
        RepetitionScreen(
            uiState = sampleState,
            onEvent = {},
            onBackPressed = {}
        )
    }
}