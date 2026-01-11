package dev.shastkiv.vocab.ui.settings.main.compose

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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditNotifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.settings.components.SettingsItemDivider
import dev.shastkiv.vocab.ui.settings.components.SettingsHeader
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onBubbleSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(
            onBackPressed = { onBackPressed() },
            title = stringResource(R.string.settings_screen_title),
            subTitle = stringResource(R.string.settings_screen_subtitle)
        )

        Spacer(Modifier.height(dimensions.smallSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumPadding)
                .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
                .background(
                    color = colors.cardBackground,
                    shape = RoundedCornerShape(dimensions.mediumCornerRadius)
                )
                .border(
                    width = 1.dp,
                    color = colors.cardBorder,
                    shape = RoundedCornerShape(dimensions.mediumCornerRadius)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.smallPadding)
            ) {
                SettingItem(
                    title = stringResource(R.string.settings_item_theme_title),
                    description = stringResource(R.string.settings_item_theme_description),
                    imageVector = Icons.Filled.DarkMode,
                    onClick = onThemeClick
                )

                SettingsItemDivider()

                SettingItem(
                    title = stringResource(R.string.settings_item_language_title),
                    description = stringResource(R.string.language_settings_item_description),
                    imageVector = Icons.Filled.Language,
                    onClick = onLanguageClick
                )

                SettingsItemDivider()

                SettingItem(
                    title = stringResource(R.string.settings_item_notifications_title),
                    description = stringResource(R.string.settings_item_notifications_description),
                    imageVector = Icons.Filled.EditNotifications,
                    onClick = onNotificationClick,
                )

                SettingsItemDivider()

                SettingItem(
                    title = stringResource(R.string.settings_item_bubble_title),
                    description = stringResource(R.string.settings_item_bubble_description),
                    imageVector = Icons.Filled.AddCircleOutline,
                    onClick = onBubbleSettingsClick
                )

                SettingsItemDivider()

                SettingItem(
                    title = stringResource(R.string.settings_item_about_title),
                    description = stringResource(R.string.settings_item_about_description),
                    imageVector = Icons.Filled.Info,
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