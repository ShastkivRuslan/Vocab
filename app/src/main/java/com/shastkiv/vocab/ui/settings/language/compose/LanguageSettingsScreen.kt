package com.shastkiv.vocab.ui.settings.language.compose

import SettingItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun LanguageSettingsScreen(
    currentSettings: LanguageSettings,
    onBackPressed: () -> Unit,
    onAppLanguageClick: () -> Unit,
    onTargetLanguageClick: () -> Unit,
    onSourceLanguageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Navigate",
                tint = MaterialTheme.customColors.cardTitleText,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onBackPressed() }
            )
            Text(
                text = stringResource(R.string.language_settings),
                fontSize = 32.sp,
                color = MaterialTheme.customColors.cardTitleText,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Text(
            modifier = Modifier.padding(start = 26.dp),
            text = stringResource(R.string.language_settings_description),
            color = MaterialTheme.customColors.cardDescriptionText,
            fontSize = 16.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = MaterialTheme.customColors.cardBackground,
                    shape = MaterialTheme.shapes.medium
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.customColors.cardBorder,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                SettingItem(
                    title = stringResource(R.string.ui_language),
                    description = "${currentSettings.appLanguage.flagEmoji} ${currentSettings.appLanguage.name}",
                    iconRes = R.drawable.ic_ui_language,
                    onClick = onAppLanguageClick
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingItem(
                    title = stringResource(R.string.target_language),
                    description = "${currentSettings.targetLanguage.flagEmoji} ${currentSettings.targetLanguage.name}",
                    iconRes = R.drawable.ic_language,
                    onClick = onTargetLanguageClick
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingItem(
                    title = stringResource(R.string.source_language),
                    description = "${currentSettings.sourceLanguage.flagEmoji} ${currentSettings.sourceLanguage.name}",
                    iconRes = R.drawable.ic_language,
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
