package com.shastkiv.vocab.ui.settings.notification

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun NotificationSettingsScreen(
    onEchoClick: () -> Unit,
    onBackPressed: () -> Unit,
    onGentleClick: () -> Unit,
    onSuccessClick: () -> Unit,
    onStreakClick: () -> Unit
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
                text = "Сповіщення",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.customColors.cardTitleText
            )
        }

        Text(
            modifier = Modifier.padding(start = 26.dp),
            text = "Тестування сповіщень",
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
            Column(modifier = Modifier.padding(10.dp)) {
                SettingItem(
                    title = "Word Echo",
                    description = "Показати слово для повторення",
                    iconRes = R.drawable.ic_theme,
                    onClick = { onEchoClick() }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingItem(
                    title = "Gentle Nudge",
                    description = "М'яке нагадування",
                    iconRes = R.drawable.ic_language,
                    onClick = { onGentleClick() }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingItem(
                    title = "Success Moment",
                    description = "Святкування успіху",
                    iconRes = R.drawable.ic_add_floating,
                    onClick = { onSuccessClick() }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingItem(
                    title = "Streak Keeper",
                    description = "Нагадування про серію",
                    iconRes = R.drawable.ic_about_us,
                    onClick = { onStreakClick() }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NotificationSettingsScreen(
        onEchoClick = {},
        onBackPressed = {},
        onSuccessClick = {},
        onGentleClick = {},
        onStreakClick = {}
    )
}