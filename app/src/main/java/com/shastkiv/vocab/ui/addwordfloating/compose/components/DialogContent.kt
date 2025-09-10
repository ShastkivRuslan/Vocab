package com.shastkiv.vocab.ui.addwordfloating.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.ui.addwordfloating.compose.components.content.*
import com.shastkiv.vocab.ui.addwordfloating.compose.state.AddWordUiState

@Composable
fun DialogContent(
    uiState: AddWordUiState,
    inputWord: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onCheckWord: () -> Unit,
    onAddToVocabulary: () -> Unit,
    onGetFullInfo: () -> Unit, // New
    onTextToSpeech: (String) -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onUsageInfoToggle: () -> Unit,
    onPaywallDismissed: () -> Unit, // New
    onSubscribe: () -> Unit, // New
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (uiState is AddWordUiState.ShowPaywall) {
            PaywallDialog(
                onDismiss = onPaywallDismissed,
                onSubscribe = onSubscribe
            )
        } else {
            DialogHeader(
                uiState = uiState,
                onTextToSpeech = onTextToSpeech,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        onGetFullInfoClick = onGetFullInfo, // Pass new callback
                        onMainInfoToggle = onMainInfoToggle,
                        onExamplesToggle = onExamplesToggle,
                        onUsageInfoToggle = onUsageInfoToggle
                    )
                }

                is AddWordUiState.SavingWord -> {
                    // This state is for Premium flow, so we assume wordData is not null
                    // You might want to adjust this if free users can also save words.
                    if (uiState.word != null) {
                        SavingWordContent(
                            state = uiState,
                            isMainSectionExpanded = uiState.isMainSectionExpanded,
                            isExamplesSectionExpanded = uiState.isExamplesSectionExpanded,
                            isContextSectionExpanded = uiState.isUsageInfoSectionExpanded,
                            onMainInfoToggle = onMainInfoToggle,
                            onExamplesToggle = onExamplesToggle,
                            ontUsageInfoToggle = onUsageInfoToggle
                        )
                    }
                }

                is AddWordUiState.Error -> {
                    ErrorContent(
                        error = uiState.type,
                        onRetry = onCheckWord
                    )
                }

                is AddWordUiState.DialogShouldClose, is AddWordUiState.ShowPaywall -> {
                    // Handled outside or above
                }
            }
        }
    }
}