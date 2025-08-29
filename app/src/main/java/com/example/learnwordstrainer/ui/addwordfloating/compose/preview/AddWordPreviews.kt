package com.example.learnwordstrainer.ui.addwordfloating.compose.preview

import androidx.compose.foundation.layout.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.domain.model.Example
import com.example.learnwordstrainer.domain.model.WordData
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.*
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.LevelBadge
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.PrimaryButton
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.WordInputField
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.content.*
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.UiError
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme

class AddWordUiStateProvider : PreviewParameterProvider<AddWordUiState> {
    override val values = sequenceOf(
        AddWordUiState.Idle,
        AddWordUiState.Loading,
        AddWordUiState.Success(PreviewData.sampleWord, isAlreadySaved = false),
        AddWordUiState.Error(UiError.NetworkError)
    )
}

object PreviewData {
    val sampleWord = WordData(
        originalWord = "Compose",
        translation = "сучасний декларативний UI інструментарій",
        transcription = "[kəmˈpoʊz]",
        partOfSpeech = "noun",
        level = "B2",
        usageInfo = "\"Англійське слово 'reusability' є формальним технічним терміном, що найчастіше зустрічається в програмуванні (reusability of code), інженерії та екології (reusability of materials). Воно має нейтральний відтінок і підкреслює ефективність та економність. Типові словосполучення: 'design for reusability' (проєктувати з розрахунком на повторне використання)",
        examples = listOf(
            Example("Compose makes UI development easier.", "Compose робить розробку UI простішою."),
            Example("We use Compose for our Android app.", "Ми використовуємо Compose для нашого Android додатку."),
            Example("Learning Compose is essential.", "Вивчення Compose є важливим.")
        )
    )

    val sampleInputWord = TextFieldValue("Compose")
    val emptyInputWord = TextFieldValue("")
}

@Preview(name = "Add Word Dialog - All States", showBackground = true)
@Composable
fun AddWordDialogPreview(
    @PreviewParameter(AddWordUiStateProvider::class) uiState: AddWordUiState
) {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            DialogContent(
                uiState = uiState,
                inputWord = when (uiState) {
                    is AddWordUiState.Idle -> PreviewData.emptyInputWord
                    else -> PreviewData.sampleInputWord
                },
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(name = "Dialog - Idle State", showBackground = true)
@Composable
fun DialogIdlePreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            DialogContent(
                uiState = AddWordUiState.Idle,
                inputWord = PreviewData.emptyInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(name = "Dialog - Loading State", showBackground = true)
@Composable
fun DialogLoadingPreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            DialogContent(
                uiState = AddWordUiState.Loading,
                inputWord = PreviewData.sampleInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(name = "Dialog - Success (Main Info)", showBackground = true)
@Composable
fun DialogSuccessMainInfoPreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            val successState = AddWordUiState.Success(
                word = PreviewData.sampleWord,
                isAlreadySaved = false,
                isMainSectionExpanded = true
            )
            Column(modifier = Modifier.padding(16.dp)) {

                DialogHeader(
                    uiState = successState,
                    onTextToSpeech = {},
                    onDismiss = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                SuccessContent(
                    state = successState,
                    onAddClick = {},
                    onMainInfoToggle = {},
                    onExamplesToggle = {},
                    onUsageInfoToggle = {}
                )
            }
        }
    }
}

@Preview(name = "Dialog - Error State", showBackground = true)
@Composable
fun DialogErrorPreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            DialogContent(
                uiState = AddWordUiState.Error(UiError.NetworkError),
                inputWord = PreviewData.sampleInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onDismiss = {}
            )
        }
    }
}

// Individual Component Previews
@Preview(name = "Dialog Header - Idle", showBackground = true)
@Composable
fun DialogHeaderIdlePreview() {
    LearnWordsTrainerTheme {
        DialogHeader(
            uiState = AddWordUiState.Idle,
            onTextToSpeech = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Dialog Header - Success", showBackground = true)
@Composable
fun DialogHeaderSuccessPreview() {
    LearnWordsTrainerTheme {
        DialogHeader(
            uiState = AddWordUiState.Success(PreviewData.sampleWord, isAlreadySaved = false),
            onTextToSpeech = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Idle Content", showBackground = true)
@Composable
fun IdleContentPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            IdleContent(
                word = TextFieldValue("Compose"),
                onWordChange = {},
                onCheckClick = {}
            )
        }
    }
}

@Preview(name = "Idle Content - Empty", showBackground = true)
@Composable
fun IdleContentEmptyPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            IdleContent(
                word = TextFieldValue(""),
                onWordChange = {},
                onCheckClick = {}
            )
        }
    }
}

@Preview(name = "Loading Content", showBackground = true)
@Composable
fun LoadingContentPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LoadingContent(word = "Compose")
        }
    }
}

@Preview(name = "Success Content - Main Expanded", showBackground = true)
@Composable
fun SuccessContentMainExpandedPreview() {
    LearnWordsTrainerTheme {
        val successState = AddWordUiState.Success(
            word = PreviewData.sampleWord,
            isAlreadySaved = false,
            isMainSectionExpanded = false
        )

        Column(modifier = Modifier.padding(16.dp)) {
            SuccessContent(
                state = successState,
                onAddClick = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {}
            )
        }
    }
}

@Preview(name = "Error Content", showBackground = true)
@Composable
fun ErrorContentPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ErrorContent(
                error = UiError.NetworkError,
                onRetry = {}
            )
        }
    }
}

@Preview(name = "Word Input Field - Empty", showBackground = true)
@Composable
fun WordInputFieldEmptyPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WordInputField(
                value = TextFieldValue(""),
                onValueChange = {},
                onClear = {}
            )
        }
    }
}

@Preview(name = "Word Input Field - Filled", showBackground = true)
@Composable
fun WordInputFieldFilledPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WordInputField(
                value = TextFieldValue("Compose"),
                onValueChange = {},
                onClear = {}
            )
        }
    }
}

@Preview(name = "Word Input Field - Read Only", showBackground = true)
@Composable
fun WordInputFieldReadOnlyPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WordInputField(
                value = TextFieldValue("Compose"),
                onValueChange = {},
                onClear = {},
                readOnly = true
            )
        }
    }
}

@Preview(name = "Primary Button - Enabled", showBackground = true)
@Composable
fun PrimaryButtonEnabledPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PrimaryButton(
                text = "Add Word",
                onClick = {},
                enabled = true
            )
        }
    }
}

@Preview(name = "Primary Button - Disabled", showBackground = true)
@Composable
fun PrimaryButtonDisabledPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PrimaryButton(
                text = "Add Word",
                onClick = {},
                enabled = false
            )
        }
    }
}

@Preview(name = "Level Badge", showBackground = true)
@Composable
fun LevelBadgePreview() {
    LearnWordsTrainerTheme {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LevelBadge("A1")
            LevelBadge("B2")
            LevelBadge("C1")
        }
    }
}

// Dark Theme Previews
@Preview(name = "Dialog - Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DialogDarkPreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            DialogContent(
                uiState = AddWordUiState.Success(PreviewData.sampleWord, isAlreadySaved = false),
                inputWord = PreviewData.sampleInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onDismiss = {}
            )
        }
    }
}

// Tablet/Large Screen Previews
@Preview(name = "Dialog - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun DialogTabletPreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            DialogContent(
                uiState = AddWordUiState.Success(PreviewData.sampleWord, isAlreadySaved = false),
                inputWord = PreviewData.sampleInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onDismiss = {}
            )
        }
    }
}