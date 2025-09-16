package com.shastkiv.vocab.ui.addword.compose.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.domain.model.Example
import com.shastkiv.vocab.ui.addword.compose.components.common.ExpandableCard
import com.shastkiv.vocab.ui.addword.compose.components.content.SuccessContent
import com.shastkiv.vocab.ui.addword.compose.components.sections.*
import com.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import com.shastkiv.vocab.ui.addword.compose.state.UserStatus
import com.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme

@Preview(name = "Word Info Section - Expanded", showBackground = true)
@Composable
fun WordInfoSectionExpandedPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WordInfoSection(
                originalWord = PreviewData.sampleWord.originalWord,
                translation = PreviewData.sampleWord.translation,
                wordData = PreviewData.sampleWord,
                isExpanded = true,
                isLocked = false,
                onToggle = {}
            )
        }
    }
}

@Preview(name = "Word Info Section - Collapsed", showBackground = true)
@Composable
fun WordInfoSectionCollapsedPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WordInfoSection(
                originalWord = PreviewData.sampleWord.originalWord,
                translation = PreviewData.sampleWord.translation,
                wordData = PreviewData.sampleWord,
                isExpanded = false,
                isLocked = false,
                onToggle = {}
            )
        }
    }
}

@Preview(name = "Examples Section - Expanded", showBackground = true)
@Composable
fun ExamplesSectionExpandedPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExamplesSection(
                examples = PreviewData.sampleWord.examples,
                isExpanded = true,
                isLocked = false,
                onToggle = {}
            )
        }
    }
}

@Preview(name = "Examples Section - Collapsed", showBackground = true)
@Composable
fun ExamplesSectionCollapsedPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExamplesSection(
                examples = PreviewData.sampleWord.examples,
                isExpanded = false,
                isLocked = false,
                onToggle = {}
            )
        }
    }
}

@Preview(name = "Single Example Card", showBackground = true)
@Composable
fun ExampleCardPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExampleCard(
                example = Example(
                    "Compose makes building UIs much easier.",
                    "Compose робить створення UI набагато простішим."
                )
            )
        }
    }
}

@Preview(name = "Expandable Card - Expanded", showBackground = true)
@Composable
fun ExpandableCardExpandedPreview() {
    var isExpanded by remember { mutableStateOf(true) }

    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExpandableCard(
                isExpanded = isExpanded,
                onToggle = { isExpanded = !isExpanded },
                title = "Expandable Section",
                showArrow = true
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    repeat(3) { index ->
                        Text("Content line ${index + 1}")
                        if (index < 2) Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Preview(name = "Expandable Card - Collapsed", showBackground = true)
@Composable
fun ExpandableCardCollapsedPreview() {
    var isExpanded by remember { mutableStateOf(false) }

    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExpandableCard(
                isExpanded = isExpanded,
                onToggle = { isExpanded = !isExpanded },
                title = "Collapsed Section",
                showArrow = true
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This content is hidden when collapsed")
                }
            }
        }
    }
}

@Preview(name = "Interactive Success Content", showBackground = true)
@Composable
fun InteractiveSuccessContentPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {

            val successState = AddWordUiState.Success(
                userStatus = UserStatus.Premium,
                originalWord = PreviewData.sampleWord.originalWord,
                wordData = PreviewData.sampleWord,
                simpleTranslation = PreviewData.sampleWord.translation,
                isAlreadySaved = false,
                isMainSectionExpanded = true
            )

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

@Preview(name = "Multiple Examples", showBackground = true)
@Composable
fun MultipleExamplesPreview() {
    val examples = listOf(
        Example("First example sentence.", "Перший приклад речення."),
        Example("Another example with Compose.", "Інший приклад з Compose."),
        Example("Final example for preview.", "Останній приклад для preview."),
        Example("Long example sentence to test how the UI handles longer content and wrapping.", "Довгий приклад речення для тестування того, як UI обробляє довший контент та перенесення.")
    )

    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExamplesSection(
                examples = examples,
                isExpanded = true,
                isLocked = false,
                onToggle = {}
            )
        }
    }
}