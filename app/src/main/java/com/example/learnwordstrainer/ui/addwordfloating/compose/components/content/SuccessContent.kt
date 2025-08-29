package com.example.learnwordstrainer.ui.addwordfloating.compose.components.content

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.PrimaryButton
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.sections.*
import com.example.learnwordstrainer.ui.addwordfloating.compose.state.AddWordUiState

@Composable
fun SuccessContent(
    state: AddWordUiState.Success,
    onAddClick: () -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onUsageInfoToggle: () -> Unit

) {

    LaunchedEffect(Unit) {
        onMainInfoToggle()
    }

    Column {
        WordInfoSection(
            word = state.word,
            isExpanded = state.isMainSectionExpanded,
            onToggle = onMainInfoToggle
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExamplesSection(
            examples = state.word.examples,
            isExpanded = state.isExamplesSectionExpanded,
            onToggle = onExamplesToggle
        )

        Spacer(modifier = Modifier.height(8.dp))

        UsageInfoSection(
            context = state.word.usageInfo,
            isExpanded = state.isUsageInfoSectionExpanded,
            onUsageInfoToggle
            )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = stringResource(R.string.add_to_vocab),
            onClick = onAddClick
        )
    }
}