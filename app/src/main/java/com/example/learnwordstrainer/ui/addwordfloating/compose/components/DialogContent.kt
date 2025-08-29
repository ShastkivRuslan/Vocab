package com.example.learnwordstrainer.ui.addwordfloating.compose.components

import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.content.*

@Composable
fun DialogContent(
    uiState: AddWordUiState,
    inputWord: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onCheckWord: () -> Unit,
    onAddToVocabulary: () -> Unit,
    onTextToSpeech: (String) -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onUsageInfoToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
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
                    onCheckClick = onCheckWord
                )
            }

            is AddWordUiState.Loading -> {
                LoadingContent(word = inputWord.text)
            }

            is AddWordUiState.Success -> {
                SuccessContent(
                    state = uiState,
                    onAddClick = { onAddToVocabulary() },
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
                    isContextSectionExpanded = uiState.isUsageInfoSectionExpanded,
                    onMainInfoToggle = onMainInfoToggle,
                    onExamplesToggle = onExamplesToggle,
                    ontUsageInfoToggle = onUsageInfoToggle
                )
            }

            is AddWordUiState.Error -> {
                ErrorContent(
                    error = uiState.type,
                    onRetry = onCheckWord
                )
            }

            is AddWordUiState.DialogShouldClose -> {}
        }
    }
}