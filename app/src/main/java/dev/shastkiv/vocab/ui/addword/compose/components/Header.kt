package dev.shastkiv.vocab.ui.addword.compose.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun Header(
    uiState: AddWordUiState,
    onTextToSpeech: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()

    ) {
            Text(
                text = getHeaderTitle(uiState),
                style = typography.sectionHeader,
                fontWeight = FontWeight.Bold,
                color = colors.textMain,
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
            tint = MaterialTheme.appColors.accent
        )
    }
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.close_bubble),
            tint = MaterialTheme.appColors.accent
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