package dev.shastkiv.vocab.ui.addword.compose.components.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.components.common.PrimaryButton
import dev.shastkiv.vocab.ui.addword.compose.components.sections.ExamplesSection
import dev.shastkiv.vocab.ui.addword.compose.components.sections.UsageInfoSection
import dev.shastkiv.vocab.ui.addword.compose.components.sections.WordInfoSection
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addword.compose.state.UserStatus
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun SuccessContent(
    state: AddWordUiState.Success,
    onAddClick: () -> Unit,
    onGetFullInfoClick: () -> Unit,
    onMainInfoToggle: () -> Unit,
    onExamplesToggle: () -> Unit,
    onUsageInfoToggle: () -> Unit
) {

    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

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

        Spacer(modifier = Modifier.height(dimensions.smallSpacing))

        ExamplesSection(
            examples = state.wordData?.examples,
            isExpanded = state.isExamplesSectionExpanded,
            isLocked = state.userStatus is UserStatus.Free,
            onToggle = onExamplesToggle
        )

        Spacer(modifier = Modifier.height(dimensions.smallSpacing))

        UsageInfoSection(
            context = state.wordData?.usageInfo,
            isExpanded = state.isUsageInfoSectionExpanded,
            isLocked = state.userStatus is UserStatus.Free,
            onToggle = onUsageInfoToggle
        )

        Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

        PrimaryButton(
            text = stringResource(R.string.add_to_vocab),
            onClick = onAddClick
        )

        if (state.userStatus is UserStatus.Free) {
            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
            PrimaryButton(
                text = stringResource(R.string.get_full_info),
                onClick = onGetFullInfoClick
            )
            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
            Text(
                text = stringResource(R.string.get_full_info_description),
                style = typography.cardDescriptionMedium,
                textAlign = TextAlign.Center,
                color = colors.cardDescriptionText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}