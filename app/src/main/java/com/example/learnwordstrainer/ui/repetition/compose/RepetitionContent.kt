package com.example.learnwordstrainer.ui.repetition.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.ui.repetition.state.RepetitionUiState
import androidx.compose.ui.tooling.preview.Preview
import com.example.learnwordstrainer.domain.model.DailyStatistic
import com.example.learnwordstrainer.domain.model.Word
import com.example.learnwordstrainer.ui.repetition.compose.components.AnswerOptions
import com.example.learnwordstrainer.ui.repetition.compose.components.ProgressCard
import com.example.learnwordstrainer.ui.repetition.compose.components.ResultFooter
import com.example.learnwordstrainer.ui.repetition.compose.components.WordCard
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme

@Composable
fun RepetitionContent(
    state: RepetitionUiState.Content,
    onAnswerClick: (Int) -> Unit,
    onNextWordClick: () -> Unit,
    onListenClick: () -> Unit,
    dailyCorrectCount: Int,
    dailyWrongCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProgressCard(
            correctCount = dailyCorrectCount,
            wrongCount = dailyWrongCount
        )

        WordCard(
            word = state.word.sourceWord,
            correctCount = state.correctCount,
            wrongCount = state.wrongCount,
            onListenClick = onListenClick
        )

        AnswerOptions(
            options = state.answerOptions,
            selectedAnswerIndex = state.selectedAnswerIndex,
            correctAnswerIndex = state.answerOptions.indexOf(state.word.translation),
            isAnswerCorrect = state.isAnswerCorrect,
            onAnswerClick = onAnswerClick
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = state.isAnswerCorrect != null,
            enter = slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ResultFooter(
                isCorrect = state.isAnswerCorrect ?: false,
                onNextWordClick = onNextWordClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepetitionContentPreview() {

    val sampleWord = Word(
        id = 1,
        sourceWord = "Heuristic",
        translation = "Евристичний",
        correctAnswerCount = 12,
        wrongAnswerCount = 3,
        sourceLanguageCode = "en",
        targetLanguageCode = "uk",
        wordLevel = "A1"
    )
    val sampleOptions = listOf("Евристичний", "Спорадичний", "Еклектичний", "Емпіричний")

    val sampleStats = DailyStatistic(
        correctAnswers = 87,
        wrongAnswers = 15
    )

    val sampleState = RepetitionUiState.Content(
        word = sampleWord,
        answerOptions = sampleOptions,
        selectedAnswerIndex = 2,
        isAnswerCorrect = false,
        dailyStats = sampleStats,
        correctCount = 10,
        wrongCount = 11
    )

    LearnWordsTrainerTheme {
        RepetitionContent(
            state = sampleState,
            dailyCorrectCount = sampleStats.correctAnswers,
            dailyWrongCount = sampleStats.wrongAnswers,
            onAnswerClick = {},
            onNextWordClick = {},
            onListenClick = {}
        )
    }
}