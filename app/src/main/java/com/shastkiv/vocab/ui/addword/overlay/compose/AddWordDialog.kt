package com.shastkiv.vocab.ui.addword.overlay.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import com.shastkiv.vocab.ui.addword.compose.components.Content
import com.shastkiv.vocab.ui.addword.compose.state.AddWordUiState

@Composable
fun AddWordDialog(
    uiState: AddWordUiState,
    inputWord: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onCheckWord: () -> Unit,
    onAddToVocabulary: () -> Unit,
    onGetFullInfo: () -> Unit,
    onTextToSpeech: (String) -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    ontUsageInfoToggle: () -> Unit,
    onPaywallDismissed: () -> Unit,
    onSubscribe: () -> Unit,
    onDismissRequest: () -> Unit
) {
    DialogContainer(onDismissRequest = onDismissRequest) {
        Content(
            uiState = uiState,
            inputWord = inputWord,
            onInputChange = onInputChange,
            onCheckWord = onCheckWord,
            onAddToVocabulary = onAddToVocabulary,
            onGetFullInfo = onGetFullInfo,
            onTextToSpeech = onTextToSpeech,
            onMainInfoToggle = onMainInfoToggle,
            onExamplesToggle = onExamplesToggle,
            onUsageInfoToggle = ontUsageInfoToggle,
            onPaywallDismissed = onPaywallDismissed,
            onSubscribe = onSubscribe,
            onDismiss = onDismissRequest
        )
    }
}