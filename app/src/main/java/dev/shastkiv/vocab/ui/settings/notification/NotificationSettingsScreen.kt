package dev.shastkiv.vocab.ui.settings.notification

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.settings.components.SettingsHeader
import dev.shastkiv.vocab.ui.settings.components.SettingsItemDivider
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun NotificationSettingsScreen(
    onEchoClick: () -> Unit,
    onBackPressed: () -> Unit,
    onGentleClick: () -> Unit,
    onSuccessClick: () -> Unit,
    onStreakClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val shapes = MaterialTheme.shapes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(
            onBackPressed = { onBackPressed() },
            title = "Сповіщення",
            subTitle = "Тестування сповіщень"
        )

        Spacer(Modifier.height(dimensions.smallSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumSpacing)
                .clip(shapes.medium)
                .background(
                    color = colors.cardBackground,
                    shape = shapes.medium
                )
                .border(
                    width = 1.dp,
                    color = colors.cardBorder,
                    shape = shapes.medium
                )
        ) {
            Column(modifier = Modifier.padding(dimensions.smallPadding)) {
                SettingItem(
                    title = "Word Echo",
                    description = "Показати слово для повторення",
                    imageVector = Icons.Filled.Repeat,
                    onClick = { onEchoClick() }
                )

                SettingsItemDivider()

                SettingItem(
                    title = "Gentle Nudge",
                    description = "М'яке нагадування",
                    imageVector = Icons.Filled.CircleNotifications,
                    onClick = { onGentleClick() }
                )

                SettingsItemDivider()

                SettingItem(
                    title = "Success Moment",
                    description = "Святкування успіху",
                    imageVector = Icons.Filled.FileDownloadDone,
                    onClick = { onSuccessClick() }
                )

                SettingsItemDivider()

                SettingItem(
                    title = "Streak Keeper",
                    description = "Нагадування про серію",
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
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