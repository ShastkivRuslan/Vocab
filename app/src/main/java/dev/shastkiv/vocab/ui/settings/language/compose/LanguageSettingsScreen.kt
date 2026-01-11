package dev.shastkiv.vocab.ui.settings.language.compose

import SettingItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.model.LanguageSettings
import dev.shastkiv.vocab.ui.settings.components.SettingsHeader
import dev.shastkiv.vocab.ui.settings.components.SettingsItemDivider
import dev.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun LanguageSettingsScreen(
    currentSettings: LanguageSettings,
    onBackPressed: () -> Unit,
    onAppLanguageClick: () -> Unit,
    onTargetLanguageClick: () -> Unit,
    onSourceLanguageClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val cornerRadius = RoundedCornerShape(dimensions.mediumCornerRadius)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(
            onBackPressed = { onBackPressed() },
            title = stringResource(R.string.language_settings),
            subTitle = stringResource(R.string.language_settings_description)
        )

        Spacer(Modifier.height(dimensions.mediumSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumPadding)
                .clip(cornerRadius)
                .background(
                    color = colors.cardBackground,
                    shape = cornerRadius
                )
                .border(
                    width = 1.dp,
                    color = colors.cardBorder,
                    shape = cornerRadius
                )
        ) {
            Column(modifier = Modifier.padding(dimensions.smallPadding)
            ) {
                SettingItem(
                    title = stringResource(R.string.ui_language),
                    description = "${currentSettings.appLanguage.flagEmoji} ${currentSettings.appLanguage.name}",
                    imageVector = Icons.Filled.Splitscreen,
                    onClick = onAppLanguageClick
                )

                SettingsItemDivider()

                SettingItem(
                    title = stringResource(R.string.target_language),
                    description = "${currentSettings.targetLanguage.flagEmoji} ${currentSettings.targetLanguage.name}",
                    imageVector = Icons.Filled.Translate,
                    onClick = onTargetLanguageClick
                )

                SettingsItemDivider()

                SettingItem(
                    title = stringResource(R.string.source_language),
                    description = "${currentSettings.sourceLanguage.flagEmoji} ${currentSettings.sourceLanguage.name}",
                    imageVector = Icons.Filled.Language,
                    onClick = onSourceLanguageClick
                )
            }
        }
    }
}
@Preview()
@Composable
fun LanguageSettingsScreenPreview() {
    val mockUkrainian = Language(code = "uk", name = "Українська", flagEmoji = "🇺🇦")
    val mockEnglish = Language(code = "en", name = "English", flagEmoji = "🇬🇧")

    val mockSettings = LanguageSettings(
        appLanguage = mockUkrainian,
        targetLanguage = mockUkrainian,
        sourceLanguage = mockEnglish
    )

    LearnWordsTrainerTheme {
        LanguageSettingsScreen(
            currentSettings = mockSettings,
            onBackPressed = {},
            onAppLanguageClick = {},
            onTargetLanguageClick = {},
            onSourceLanguageClick = {}
        )
    }
}
