package dev.shastkiv.vocab.ui.quiz.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.shastkiv.vocab.domain.model.DailyStatistic
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.ui.quiz.compose.components.AnswerOptions
import dev.shastkiv.vocab.ui.quiz.compose.components.ResultFooter
import dev.shastkiv.vocab.ui.quiz.compose.components.WordCard
import dev.shastkiv.vocab.ui.quiz.state.RepetitionUiState
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun RepetitionContent(
    state: RepetitionUiState.Content,
    onAnswerClick: (Int) -> Unit,
    onNextWordClick: () -> Unit,
    onListenClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
    ) {
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
        targetLanguageCode = "uk"
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

        RepetitionContent(
            state = sampleState,
            onAnswerClick = {},
            onNextWordClick = {},
            onListenClick = {}
        )
}