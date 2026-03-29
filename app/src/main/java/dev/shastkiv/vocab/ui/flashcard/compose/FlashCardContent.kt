package dev.shastkiv.vocab.ui.flashcard.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.ui.flashcard.compose.components.FlashCardButtons
import dev.shastkiv.vocab.ui.flashcard.compose.components.FlashCardDeck
import dev.shastkiv.vocab.ui.flashcard.state.FlashCardUiState
import dev.shastkiv.vocab.ui.quiz.compose.components.ResultFooter
import dev.shastkiv.vocab.ui.quiz.compose.components.VocabProgressBar
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun FlashCardContent(
    state: FlashCardUiState.Content,
    onKnow: () -> Unit,
    onDontKnow: () -> Unit,
    onNextCard: () -> Unit,
    onListenClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions

    Box(modifier = Modifier.fillMaxSize()) {

        // Верхня частина — прогрес + стек карток
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VocabProgressBar(
                totalSteps = state.totalSteps,
                currentStep = state.currentStep,
                segmentStates = state.segmentStates
            )

            Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

            // Стек карток — передаємо поточне слово + наступні для відображення колоди
            FlashCardDeck(
                currentWord = state.word,
                nextWords = state.nextWords,           // список наступних слів зі стану
                isAnswered = state.isAnswered,
                isAnswerCorrect = state.isAnswerCorrect,
                onKnow = onKnow,
                onDontKnow = onDontKnow,
                onListenClick = onListenClick,
                onExitComplete = onNextCard,           // ViewModel переходить до наступного
                modifier = Modifier.padding(horizontal = dimensions.mediumPadding)
            )
        }

        // Нижня частина — кнопки або ResultFooter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Кнопки знаю / не знаю — зникають після відповіді
            AnimatedVisibility(
                visible = !state.isAnswered,
                enter = fadeIn(tween(200)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(200)
                ),
                exit = fadeOut(tween(180)) + slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(180)
                )
            ) {
                FlashCardButtons(
                    enabled = true,
                    onKnow = onKnow,
                    onDontKnow = onDontKnow,
                    modifier = Modifier.padding(dimensions.mediumPadding)
                )
            }

            // ResultFooter — з'являється після відповіді
            // onNextWordClick тут є запасним варіантом (якщо користувач тапнув вручну)
            // основний перехід відбувається через onExitComplete після анімації картки
            AnimatedVisibility(
                visible = state.isAnswered,
                enter = fadeIn(tween(300)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(300)
                ),
                exit = fadeOut(tween(200)) + slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(200)
                )
            ) {
                ResultFooter(
                    isCorrect = state.isAnswerCorrect ?: false,
                    onNextWordClick = onNextCard
                )
            }
        }
    }
}