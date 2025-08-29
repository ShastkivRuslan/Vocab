package com.example.learnwordstrainer.ui.addwordfloating.compose.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.domain.model.Example
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.ExpandableCard
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.content.SuccessContent
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.sections.*
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme

@Preview(name = "Word Info Section - Expanded", showBackground = true)
@Composable
fun WordInfoSectionExpandedPreview() {
    LearnWordsTrainerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            WordInfoSection(
                word = PreviewData.sampleWord,
                isExpanded = true,
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
                word = PreviewData.sampleWord,
                isExpanded = false,
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
                word = PreviewData.sampleWord,
                isAlreadySaved = false,
                isMainSectionExpanded = true
            )

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
                onToggle = {}
            )
        }
    }
}