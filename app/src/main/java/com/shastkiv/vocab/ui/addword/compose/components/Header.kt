package com.shastkiv.vocab.ui.addword.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.addword.compose.state.AddWordUiState

@Composable
fun Header(
    uiState: AddWordUiState,
    onTextToSpeech: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()

    ) {
            Text(
                text = getHeaderTitle(uiState),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

        if (shouldShowSpeakerButton(uiState)) {
            val word = when (uiState) {
                is AddWordUiState.Success -> uiState.originalWord
                is AddWordUiState.SavingWord -> uiState.word.originalWord
                else -> ""
            }
            if (word.isNotEmpty()) {
                SpeakerButton(
                    onClick = { onTextToSpeech(word) }
                )
            }
        }

        CloseButton(onClick = onDismiss)
    }
}

@Composable
private fun SpeakerButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_speaker),
            contentDescription = stringResource(R.string.tts_button),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.close_bubble),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun getHeaderTitle(uiState: AddWordUiState): String {
    return when (uiState) {
        is AddWordUiState.Success -> uiState.originalWord
        is AddWordUiState.SavingWord -> uiState.word.originalWord
        else -> stringResource(R.string.add_word_title)
    }
}

private fun shouldShowSpeakerButton(uiState: AddWordUiState): Boolean {
    return when (uiState) {
        is AddWordUiState.Success -> true
        is AddWordUiState.SavingWord -> uiState.shouldShowSections
        else -> false
    }
}