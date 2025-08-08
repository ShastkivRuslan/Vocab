package com.example.learnwordstrainer.ui.addwordfloating.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.DialogContainer
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.DialogContent
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState

@Composable
fun AddWordDialog(
    uiState: AddWordUiState,
    inputWord: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onCheckWord: () -> Unit,
    onAddToVocabulary: () -> Unit,
    onTextToSpeech: (String) -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    ontContextToggle: () -> Unit,
    onDismissRequest: () -> Unit
) {
    DialogContainer(onDismissRequest = onDismissRequest) {
        DialogContent(
            uiState = uiState,
            inputWord = inputWord,
            onInputChange = onInputChange,
            onCheckWord = onCheckWord,
            onAddToVocabulary = onAddToVocabulary,
            onTextToSpeech = onTextToSpeech,
            onMainInfoToggle = onMainInfoToggle,
            onExamplesToggle = onExamplesToggle,
            onContextToggle = ontContextToggle,
            onDismiss = onDismissRequest
        )
    }
}