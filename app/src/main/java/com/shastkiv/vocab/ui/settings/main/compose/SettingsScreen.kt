package com.shastkiv.vocab.ui.settings.main.compose

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.R

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
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_left_chevron),
                    contentDescription = stringResource(R.string.back_button_description),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = stringResource(R.string.settings_screen_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            modifier = Modifier.padding(start = 26.dp),
            text = stringResource(R.string.settings_screen_subtitle),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge
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