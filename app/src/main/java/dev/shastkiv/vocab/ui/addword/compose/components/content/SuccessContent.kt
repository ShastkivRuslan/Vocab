package dev.shastkiv.vocab.ui.addword.compose.components.content

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.components.common.PrimaryButton
import dev.shastkiv.vocab.ui.addword.compose.components.sections.*
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addword.compose.state.UserStatus

@Composable
fun SuccessContent(
    state: AddWordUiState.Success,
    onAddClick: () -> Unit,
    onGetFullInfoClick: () -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onUsageInfoToggle: () -> Unit
) {

    LaunchedEffect(state.userStatus) {
        if (state.userStatus is UserStatus.Premium && !state.isMainSectionExpanded && !state.isExamplesSectionExpanded && !state.isUsageInfoSectionExpanded) {
            onMainInfoToggle()
        }
    }

    Column {
        WordInfoSection(
            originalWord = state.originalWord,
            translation = state.simpleTranslation,
            wordData = state.wordData,
            isExpanded = state.isMainSectionExpanded,
            isLocked = state.userStatus is UserStatus.Free,
            onToggle = onMainInfoToggle
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExamplesSection(
            examples = state.wordData?.examples,
            isExpanded = state.isExamplesSectionExpanded,
            isLocked = state.userStatus is UserStatus.Free,
            onToggle = onExamplesToggle
        )

        Spacer(modifier = Modifier.height(8.dp))

        UsageInfoSection(
            context = state.wordData?.usageInfo,
            isExpanded = state.isUsageInfoSectionExpanded,
            isLocked = state.userStatus is UserStatus.Free,
            onToggle = onUsageInfoToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = stringResource(R.string.add_to_vocab),
            onClick = onAddClick
        )

        if (state.userStatus is UserStatus.Free) {
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = stringResource(R.string.get_full_info),
                onClick = onGetFullInfoClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.get_full_info_description),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}