package com.shastkiv.vocab.ui.settings.language.compose

import SettingItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme

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
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed, modifier = Modifier.size(48.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_left_chevron),
                    contentDescription = "Back"
                )
            }
            Text(
                text = stringResource(R.string.language_settings),
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Text(
            modifier = Modifier.padding(start = 26.dp),
            text = stringResource(R.string.language_settings_description),
            color = Color.White,
            fontSize = 16.sp
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed, modifier = Modifier.size(48.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_left_chevron),
                    contentDescription = "Back"
                )
            }
            Text(
                text = stringResource(R.string.language_settings),
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Text(
            modifier = Modifier.padding(start = 26.dp),
            text = stringResource(R.string.language_settings_description),
            color = Color.White,
            fontSize = 16.sp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            elevation = CardDefaults.cardElevation(7.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
