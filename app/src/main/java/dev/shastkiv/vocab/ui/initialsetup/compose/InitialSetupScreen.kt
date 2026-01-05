package dev.shastkiv.vocab.ui.initialsetup.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import dev.shastkiv.vocab.ui.initialsetup.InitialSetupViewModel
import dev.shastkiv.vocab.ui.initialsetup.compose.content.InterfaceLanguageContent
import dev.shastkiv.vocab.ui.initialsetup.compose.content.NotificationContent
import dev.shastkiv.vocab.ui.initialsetup.compose.content.OverlayContent
import dev.shastkiv.vocab.ui.initialsetup.compose.content.TranslationLanguagesContent
import dev.shastkiv.vocab.ui.initialsetup.compose.state.SetupStep
import dev.shastkiv.vocab.ui.theme.dimensions

@Composable
fun InitialSetupScreen(
    viewModel: InitialSetupViewModel,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(SetupStep.INTERFACE_LANGUAGE) }
    var isGoingForward by remember { mutableStateOf(true) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val progress by animateFloatAsState(
        targetValue = when (currentStep) {
            SetupStep.INTERFACE_LANGUAGE -> 0.25f
            SetupStep.TRANSLATION_LANGUAGES -> 0.5f
            SetupStep.PERMISSION_OVERLAY -> 0.75f
            SetupStep.PERMISSION_NOTIFICATIONS -> 1f
        },
        animationSpec = tween(durationMillis = 400),
        label = "progress"
    )

    val dimensions = MaterialTheme.dimensions
    val defaultColors = MaterialTheme.colorScheme

    Column(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (isGoingForward) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                label = "content_transition"
            ) { step ->
                when (step) {
                    SetupStep.INTERFACE_LANGUAGE -> {
                        InterfaceLanguageContent(
                            viewModel = viewModel,
                            isLoading = isLoading,
                            onContinue = {
                                isGoingForward = true
                                currentStep = SetupStep.TRANSLATION_LANGUAGES
                            }
                        )
                    }
                    SetupStep.TRANSLATION_LANGUAGES -> {
                        TranslationLanguagesContent(
                            isLoading = isLoading,
                            error = error,
                            onLanguagesSelected = { source, target ->
                                viewModel.saveTranslationLanguages(source, target)
                                isGoingForward = true
                                currentStep = SetupStep.PERMISSION_OVERLAY
                            },
                            onBackPressed = {
                                isGoingForward = false
                                currentStep = SetupStep.INTERFACE_LANGUAGE
                            },
                            onErrorDismissed = {
                                viewModel.clearError()
                            }
                        )
                    }
                    SetupStep.PERMISSION_OVERLAY -> {
                        OverlayContent(
                            onSkipPressed = {
                                isGoingForward = true
                                currentStep = SetupStep.PERMISSION_NOTIFICATIONS
                            },
                            onBackPressed = {
                                isGoingForward = false
                                currentStep = SetupStep.TRANSLATION_LANGUAGES
                            }
                        )
                    }
                    SetupStep.PERMISSION_NOTIFICATIONS -> {
                        NotificationContent(
                            onSkipPressed = {
                                viewModel.completeSetup()
                                onComplete()
                            },
                            onConfirmPressed = {
                                viewModel.completeSetup()
                                onComplete()
                            },
                            onBackPressed = {
                                isGoingForward = false
                                currentStep = SetupStep.PERMISSION_OVERLAY
                            }

                        )
                    }
                }
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumPadding)
                .padding(bottom = dimensions.spacingMedium),
            color = defaultColors.primary,
            trackColor = defaultColors.primary.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
    }
}