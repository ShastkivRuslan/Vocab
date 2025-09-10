package com.shastkiv.vocab.ui.addwordfloating.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import com.shastkiv.vocab.ui.addwordfloating.compose.components.DialogContainer
import com.shastkiv.vocab.ui.addwordfloating.compose.components.DialogContent
import com.shastkiv.vocab.ui.addwordfloating.compose.state.AddWordUiState

@Composable
fun AddWordDialog(
    uiState: AddWordUiState,
    inputWord: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onCheckWord: () -> Unit,
    onAddToVocabulary: () -> Unit,
    onGetFullInfo: () -> Unit, // New
    onTextToSpeech: (String) -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    ontUsageInfoToggle: () -> Unit,
    onPaywallDismissed: () -> Unit, // New
    onSubscribe: () -> Unit, // New
    onDismissRequest: () -> Unit
) {
    DialogContainer(onDismissRequest = onDismissRequest) {
        DialogContent(
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