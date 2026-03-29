package dev.shastkiv.vocab.ui.flashcard.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.common.compose.ErrorContent
import dev.shastkiv.vocab.ui.flashcard.FlashCardEvent
import dev.shastkiv.vocab.ui.flashcard.FlashCardViewModel
import dev.shastkiv.vocab.ui.flashcard.state.FlashCardUiState
import dev.shastkiv.vocab.ui.quiz.compose.LobbyContent
import dev.shastkiv.vocab.ui.quiz.compose.SessionFinishedContent
import dev.shastkiv.vocab.ui.quiz.state.RepetitionUiState
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun FlashCardScreen(
    viewModel: FlashCardViewModel,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.padding(dimensions.mediumPadding),
            verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensions.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = colors.cardTitleText,
                    modifier = Modifier
                        .size(dimensions.headerIconSize)
                        .clickable { onBackPressed() }
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = dimensions.mediumPadding),
                    text = stringResource(R.string.flashcards_mode),
                    style = typography.header,
                    color = colors.textMain
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is FlashCardUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is FlashCardUiState.Lobby -> {
                        // Перевикористовуємо LobbyContent з квізу
                        LobbyContent(
                            state = RepetitionUiState.Lobby(
                                dailyStats = state.dailyStats,
                                wordsCategorySelected = state.wordsCategorySelected,
                                counts = state.counts
                            ),
                            onStartClick = {
                                viewModel.onEvent(FlashCardEvent.OnStartSessionClicked)
                            },
                            onCategoryChange = { category ->
                                viewModel.onEvent(FlashCardEvent.OnCategoryChanged(category))
                                viewModel.onEvent(FlashCardEvent.OnStartSessionClicked)
                            }
                        )
                    }

                    is FlashCardUiState.Content -> {
                        FlashCardContent(
                            state = state,
                            onKnow = { viewModel.onEvent(FlashCardEvent.OnKnowClicked) },
                            onDontKnow = { viewModel.onEvent(FlashCardEvent.OnDontKnowClicked) },
                            onNextCard = { viewModel.onEvent(FlashCardEvent.OnNextCardClicked) },
                            onListenClick = { viewModel.onEvent(FlashCardEvent.OnListenClicked) }
                        )
                    }

                    is FlashCardUiState.SessionFinished -> {
                        SessionFinishedContent(
                            state = RepetitionUiState.SessionFinished(
                                totalCorrect = state.totalCorrect,
                                totalWrong = state.totalWrong,
                                masteryEarned = state.masteryEarned,
                                hasWrongAnswers = state.totalWrong > 0
                            ),
                            onContinueClick = {
                                viewModel.onEvent(FlashCardEvent.OnReturnToLobby)
                            },
                            onFinishClick = onBackPressed,
                            onMistakesReviewClick = {
                                viewModel.onEvent(FlashCardEvent.OnReturnToLobby)
                            }
                        )
                    }

                    is FlashCardUiState.Error -> {
                        ErrorContent(
                            error = state.error,
                            onRetry = { viewModel.onEvent(FlashCardEvent.OnStartSessionClicked) },
                            modifier = Modifier.fillMaxSize(),
                            showButton = true
                        )
                    }
                }
            }
        }
    }
}