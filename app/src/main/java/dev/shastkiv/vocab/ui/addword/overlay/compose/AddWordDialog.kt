package dev.shastkiv.vocab.ui.addword.overlay.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import dev.shastkiv.vocab.ui.addword.compose.components.Content
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState

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
    onDismissRequest: () -> Unit,
    onRetryManual: () -> Unit,
    isListening: Boolean = false,
    isReady: Boolean = false,
    rmsLevel: Float = 0f,
    onSpeechToText: () -> Unit = {}
) {
    val isLoading = uiState is AddWordUiState.Loading ||
            (uiState is AddWordUiState.SavingWord && uiState.shouldShowLoader)

    val stateType = when (uiState) {
        is AddWordUiState.Idle          -> 0
        is AddWordUiState.Loading       -> 1
        is AddWordUiState.Success       -> 2
        is AddWordUiState.SavingWord    -> 3
        is AddWordUiState.Error         -> 4
        is AddWordUiState.Warning       -> 5
        is AddWordUiState.ShowPaywall   -> 6
        is AddWordUiState.DialogShouldClose -> 7
    }

    DialogContainer(
        isLoading = isLoading,
        pulseKey = stateType,
        onDismissRequest = onDismissRequest
    ) {
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
            onDismiss = onDismissRequest,
            onRetryManual = onRetryManual,
            isListening = isListening,
            isReady = isReady,
            rmsLevel = rmsLevel,
            onSpeechToText = onSpeechToText
        )
    }
}