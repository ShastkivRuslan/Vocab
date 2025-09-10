package com.shastkiv.vocab.ui.addwordfloating.compose.components.content

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.addwordfloating.compose.components.common.PrimaryButton
import com.shastkiv.vocab.ui.addwordfloating.compose.components.sections.*
import com.shastkiv.vocab.ui.addwordfloating.compose.state.AddWordUiState
import com.shastkiv.vocab.ui.addwordfloating.compose.state.UserStatus

@Composable
fun SuccessContent(
    state: AddWordUiState.Success,
    onAddClick: () -> Unit,
    onGetFullInfoClick: () -> Unit, // New callback
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onUsageInfoToggle: () -> Unit
) {

    LaunchedEffect(state.userStatus) {
        // Automatically expand main section for premium users on first load
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

        // --- Buttons and Promo Text ---
        PrimaryButton(
            text = stringResource(R.string.add_to_vocab),
            onClick = onAddClick
        )

        if (state.userStatus is UserStatus.Free) {
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(
                text = "Отримати повну інформацію ✨",
                onClick = onGetFullInfoClick
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Розблокуйте приклади та контекст з Premium-підпискою.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}