package com.shastkiv.vocab.ui.settings.main.compose

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
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onBubbleSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = stringResource(R.string.settings_screen_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.customColors.cardTitleText
            )
        }

        Text(
            modifier = Modifier.padding(start = 26.dp),
            text = stringResource(R.string.settings_screen_subtitle),
            color = MaterialTheme.customColors.cardDescriptionText,
            style = MaterialTheme.typography.bodyLarge
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                SettingItem(
                    title = stringResource(R.string.settings_item_theme_title),
                    description = stringResource(R.string.settings_item_theme_description),
                    iconRes = R.drawable.ic_theme,
                    onClick = onThemeClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingItem(
                    title = stringResource(R.string.settings_item_language_title),
                    description = stringResource(R.string.language_settings_item_description),
                    iconRes = R.drawable.ic_language,
                    onClick = onLanguageClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingItem(
                    title = stringResource(R.string.settings_item_notifications_title),
                    description = stringResource(R.string.settings_item_notifications_description),
                    iconRes = R.drawable.ic_notification,
                    onClick = onNotificationClick,
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingItem(
                    title = stringResource(R.string.settings_item_bubble_title),
                    description = stringResource(R.string.settings_item_bubble_description),
                    iconRes = R.drawable.ic_add_floating,
                    onClick = onBubbleSettingsClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingItem(
                    title = stringResource(R.string.settings_item_about_title),
                    description = stringResource(R.string.settings_item_about_description),
                    iconRes = R.drawable.ic_about_us,
                    onClick = onAboutClick
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(
        onBackPressed = {},
        onThemeClick = {},
        onLanguageClick = {},
        onNotificationClick = {},
        onBubbleSettingsClick = {},
        onAboutClick = {}
    )
}