package dev.shastkiv.vocab.ui.addword.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import dev.shastkiv.vocab.ui.addword.compose.components.content.ErrorContent
import dev.shastkiv.vocab.ui.addword.compose.components.content.IdleContent
import dev.shastkiv.vocab.ui.addword.compose.components.content.LoadingContent
import dev.shastkiv.vocab.ui.addword.compose.components.content.SavingWordContent
import dev.shastkiv.vocab.ui.addword.compose.components.content.SuccessContent
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addwordfloating.compose.components.PaywallDialog
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun Content(
    uiState: AddWordUiState,
    inputWord: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onCheckWord: () -> Unit,
    onAddToVocabulary: () -> Unit,
    onGetFullInfo: () -> Unit,
    onTextToSpeech: (String) -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onUsageInfoToggle: () -> Unit,
    onPaywallDismissed: () -> Unit,
    onSubscribe: () -> Unit,
    onDismiss: () -> Unit,
    onRetryManual: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions

    Column(modifier = Modifier.padding(dimensions.mediumPadding)) {
        if (uiState is AddWordUiState.ShowPaywall) {
            PaywallDialog(
                onDismiss = onPaywallDismissed,
                onSubscribe = onSubscribe
            )
        } else {
            Header(
                uiState = uiState,
                onTextToSpeech = onTextToSpeech,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

            when (uiState) {
                is AddWordUiState.Idle -> {
                    IdleContent(
                        word = inputWord,
                        onWordChange = onInputChange,
                        onCheckClick = onCheckWord,
                        userStatus = uiState.userStatus
                    )
                }

                is AddWordUiState.Loading -> {
                    LoadingContent(word = inputWord.text)
                }

                is AddWordUiState.Success -> {
                    SuccessContent(
                        state = uiState,
                        onAddClick = { onAddToVocabulary() },
                        onGetFullInfoClick = onGetFullInfo,
                        onMainInfoToggle = onMainInfoToggle,
                        onExamplesToggle = onExamplesToggle,
                        onUsageInfoToggle = onUsageInfoToggle
                    )
                }

                is AddWordUiState.SavingWord -> {
                    SavingWordContent(
                        state = uiState,
                        isMainSectionExpanded = uiState.isMainSectionExpanded,
                        isExamplesSectionExpanded = uiState.isExamplesSectionExpanded,
                        isContextSectionExpanded = uiState.isUsageInfoSectionExpanded
                    )
                }

                is AddWordUiState.Error -> {
                    ErrorContent(
                        error = uiState.type,
                        onRetry = onCheckWord
                    )
                }

                is AddWordUiState.Warning -> {
                    ErrorContent(
                        error = uiState.type,
                        onRetry = onRetryManual
                    )
                }

                is AddWordUiState.DialogShouldClose, is AddWordUiState.ShowPaywall -> {
                }
            }
        }
    }
}