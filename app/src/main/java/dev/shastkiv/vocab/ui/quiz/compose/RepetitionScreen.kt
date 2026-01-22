package dev.shastkiv.vocab.ui.quiz.compose

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
import dev.shastkiv.vocab.ui.quiz.RepetitionEvent
import dev.shastkiv.vocab.ui.quiz.RepetitionViewModel
import dev.shastkiv.vocab.ui.quiz.compose.components.ProgressCard
import dev.shastkiv.vocab.ui.quiz.state.RepetitionUiState
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun RepetitionScreen(
    viewModel: RepetitionViewModel,
    uiState: RepetitionUiState,
    onEvent: (RepetitionEvent) -> Unit,
    onBackPressed: () -> Unit
) {
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    val stats by viewModel.dailyStats.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding())
    {
        Column(modifier = Modifier.padding(dimensions.mediumPadding),
            verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensions.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.appColors.cardTitleText,
                    modifier = Modifier
                        .size(dimensions.headerIconSize)
                        .clickable { onBackPressed() }
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = dimensions.mediumPadding),
                    text = stringResource(R.string.repeat_mode),
                    style = typography.header,
                    color = colors.textMain
                )
            }


            if (uiState !is RepetitionUiState.Error) {
                ProgressCard(
                    correctCount = stats?.correctAnswers ?: 0,
                    wrongCount = stats?.wrongAnswers ?: 0
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (uiState) {
                    is RepetitionUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is RepetitionUiState.Content -> {
                        RepetitionContent(
                            state = uiState,
                            onAnswerClick = { index ->
                                onEvent(
                                    RepetitionEvent.OnAnswerSelected(
                                        index
                                    )
                                )
                            },
                            onNextWordClick = { onEvent(RepetitionEvent.OnNextWordClicked) },
                            onListenClick = { onEvent(RepetitionEvent.OnListenClicked) }
                        )
                    }

                    is RepetitionUiState.Error -> {
                        ErrorContent(
                            error = uiState.error,
                            onRetry = { onEvent(RepetitionEvent.OnNextWordClicked) },
                            modifier = Modifier.fillMaxSize(),
                            showButton = false
                        )
                    }
                }
            }
        }
    }
}