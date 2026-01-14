package dev.shastkiv.vocab.ui.addword.compose.preview

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.domain.model.Example
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.ui.addword.compose.components.Content
import dev.shastkiv.vocab.ui.addword.overlay.compose.DialogContainer
import dev.shastkiv.vocab.ui.addword.compose.components.content.SuccessContent
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addword.compose.state.UserStatus
import dev.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme

class AddWordUiStateProvider : PreviewParameterProvider<AddWordUiState> {
    override val values = sequenceOf(
        AddWordUiState.Idle(
            userStatus = UserStatus.Free
        ),
        AddWordUiState.Loading,
        AddWordUiState.Success(
            userStatus = UserStatus.Premium,
            originalWord = PreviewData.sampleWord.originalWord,
            wordData = PreviewData.sampleWord,
            simpleTranslation = PreviewData.sampleWord.translation,
            isAlreadySaved = false,
            isMainSectionExpanded = true
        ),
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
            Content(
                uiState = uiState,
                inputWord = when (uiState) {
                    is AddWordUiState.Idle -> PreviewData.emptyInputWord
                    else -> PreviewData.sampleInputWord
                },
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onGetFullInfo = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onPaywallDismissed = {},
                onSubscribe = {},
                onDismiss = {},
                onRetryManual = {}
            )
        }
    }
}

@Preview(name = "Dialog - Success (Free User)", showBackground = true)
@Composable
fun DialogSuccessFreeUserPreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            val successState = AddWordUiState.Success(
                userStatus = UserStatus.Free,
                originalWord = PreviewData.sampleWord.originalWord,
                wordData = null,
                simpleTranslation = "Компонувати",
                isAlreadySaved = false,
                isMainSectionExpanded = true
            )
            Content(
                uiState = successState,
                inputWord = PreviewData.sampleInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onGetFullInfo = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onPaywallDismissed = {},
                onSubscribe = {},
                onDismiss = {},
                onRetryManual = {}
            )
        }
    }
}


@Preview(name = "Dialog - Success (Premium User)", showBackground = true)
@Composable
fun DialogSuccessPremiumUserPreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            val successState = AddWordUiState.Success(
                userStatus = UserStatus.Premium,
                wordData = PreviewData.sampleWord,
                originalWord = PreviewData.sampleWord.originalWord,
                simpleTranslation = PreviewData.sampleWord.translation,
                isAlreadySaved = false,
                isMainSectionExpanded = true
            )
            Content(
                uiState = successState,
                inputWord = PreviewData.sampleInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onGetFullInfo = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onPaywallDismissed = {},
                onSubscribe = {},
                onDismiss = {},
                onRetryManual = {}
            )
        }
    }
}

@Preview(name = "Dialog - Idle State", showBackground = true)
@Composable
fun DialogIdlePreview() {
    LearnWordsTrainerTheme {
        DialogContainer(onDismissRequest = {}) {
            Content(
                uiState = AddWordUiState.Idle(userStatus = UserStatus.Free  ),
                inputWord = PreviewData.emptyInputWord,
                onInputChange = {},
                onCheckWord = {},
                onAddToVocabulary = {},
                onGetFullInfo = {},
                onTextToSpeech = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {},
                onPaywallDismissed = {},
                onSubscribe = {},
                onDismiss = {},
                onRetryManual = {}
            )
        }
    }
}

@Preview(name = "Success Content - Free", showBackground = true)
@Composable
fun SuccessContentFreePreview() {
    LearnWordsTrainerTheme {
        val successState = AddWordUiState.Success(
            userStatus = UserStatus.Free,
            originalWord = "Reusability",
            wordData = PreviewData.sampleWord,
            simpleTranslation = "Повторне використання",
            isAlreadySaved = false,
            isMainSectionExpanded = true
        )

        Column(modifier = Modifier.padding(16.dp)) {
            SuccessContent(
                state = successState,
                onAddClick = {},
                onGetFullInfoClick = {},
                onMainInfoToggle = {},
                onExamplesToggle = {},
                onUsageInfoToggle = {}
            )
        }
    }
}