package dev.shastkiv.vocab.ui.addword.external.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.addword.compose.components.Content
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addword.shared.AddWordViewModel
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import kotlinx.coroutines.delay

@Composable
fun ProcessTextScreen(
    viewModel: AddWordViewModel,
    initialText: String?,
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputWord by viewModel.inputWord.collectAsState()

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    LaunchedEffect(initialText) {
        if (!initialText.isNullOrBlank()) {
            viewModel.initialize(initialText)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddWordUiState.DialogShouldClose -> {
                isVisible = false
            }
            else -> {}
        }
    }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(ANIMATION_DURATION_MS.toLong())
            onFinish()
        }
    }
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.textProcessingBackgroundColor)
            .clickable { isVisible = false },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)) +
                    scaleIn(animationSpec = tween(ANIMATION_DURATION_MS), initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MS)) +
                    scaleOut(animationSpec = tween(ANIMATION_DURATION_MS), targetScale = 0.8f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.largePadding)
                    .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
                    .background(
                        colors.textProcessingCardColor
                    )
                    .border(
                        width = 1.dp,
                        brush = colors.expandableCardBorder,
                        shape = RoundedCornerShape(dimensions.mediumCornerRadius)
                    )
                    .clickable(enabled = false) {}
            ) {
                Content(
                    uiState = uiState,
                    inputWord = inputWord,
                    onInputChange = viewModel::onInputChange,
                    onCheckWord = viewModel::onCheckWord,
                    onAddToVocabulary = viewModel::onAddWord,
                    onGetFullInfo = viewModel::onGetFullInfoClicked,
                    onTextToSpeech = viewModel::onTextToSpeech,
                    onMainInfoToggle = viewModel::onMainInfoToggle,
                    onExamplesToggle = viewModel::onExamplesToggle,
                    onUsageInfoToggle = viewModel::ontUsageInfoToggle,
                    onPaywallDismissed = viewModel::onPaywallDismissed,
                    onSubscribe = { isVisible = false },
                    onDismiss = { isVisible = false },
                    onRetryManual = viewModel::onRetryToIdle
                )
            }
        }
    }
}

private const val ANIMATION_DURATION_MS = 300