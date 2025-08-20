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
                fontSize = 32.sp,
                color = Color.White
            )
        }

        // Основний контент екрану, що займає решту місця
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f) // Це важливо, щоб контент займав весь доступний простір
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
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LottieAnimation(
                                composition = composition,
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = uiState.message,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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
