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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.ui.quiz.compose.components.AnswerOptions
import dev.shastkiv.vocab.ui.quiz.compose.components.ResultFooter
import dev.shastkiv.vocab.ui.quiz.compose.components.VocabProgressBar
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
    ) {
        VocabProgressBar(
            totalSteps = state.totalSteps,
            currentStep = state.currentStep,
            segmentStates = state.segmentStates
        )

        Spacer(modifier = Modifier.height(MaterialTheme.appDimensions.smallSpacing))

        key(state.word.id) {
            WordCard(
                word = state.word,
                onListenClick = onListenClick
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.appDimensions.smallSpacing))

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
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)
                )
            ,
            exit = slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(300))
                    + fadeOut(animationSpec = tween(300)
                )
            ,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ResultFooter(
                isCorrect = state.isAnswerCorrect ?: false,
                onNextWordClick = onNextWordClick
            )
        }
    }
}