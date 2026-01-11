import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.bubble.BubbleSettingsUiState
import dev.shastkiv.vocab.ui.settings.components.BubbleSettingItem
import dev.shastkiv.vocab.ui.settings.components.SettingItemWithSwitch
import dev.shastkiv.vocab.ui.settings.components.SettingsSlider
import dev.shastkiv.vocab.ui.settings.components.SettingsHeader
import dev.shastkiv.vocab.ui.settings.components.SettingsItemDivider
import dev.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun BubbleSettingsScreen(
    uiState: BubbleSettingsUiState,
    onBackPressed: () -> Unit,
    onBubbleEnabledChange: (Boolean) -> Unit,
    onBubbleSizeChange: (Float) -> Unit,
    onTransparencyChange: (Float) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    onAutoHideClick: () -> Unit,
    onAboutBubbleClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val shapes = MaterialTheme.shapes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(
            onBackPressed = { onBackPressed() },
            title = stringResource(R.string.bubble_settings_screen_title),
            subTitle = stringResource(R.string.bubble_settings_screen_subtitle)
        )

        Spacer(Modifier.height(dimensions.smallSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shapes.medium)
                .padding(horizontal = dimensions.mediumPadding)
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
            Column(modifier = Modifier.padding(dimensions.smallPadding)
            ) {
                SettingItemWithSwitch(
                    title = stringResource(R.string.bubble_enable_title),
                    description = stringResource(R.string.bubble_enable_description),
                    isChecked = uiState.isBubbleEnabled,
                    onCheckedChange = onBubbleEnabledChange
                )

                SettingsItemDivider()

                SettingsSlider(
                    title = stringResource(R.string.bubble_size_title),
                    description = stringResource(R.string.bubble_size_description),
                    currentValue = uiState.bubbleSize,
                    valueRange = 30f..80f,
                    steps = 9,
                    onValueChangeFinished = { onBubbleSizeChange(it) }
                )

                SettingsItemDivider()

                SettingsSlider(
                    title = stringResource(R.string.bubble_transparency_title),
                    description = stringResource(R.string.bubble_transparency_description),
                    currentValue = uiState.bubbleTransparency,
                    valueRange = 0f..100f,
                    steps = 10,
                    onValueChangeFinished = { onTransparencyChange(it) }
                )

                SettingsItemDivider()

                SettingItemWithSwitch(
                    title = stringResource(R.string.bubble_vibration_title),
                    description = stringResource(R.string.bubble_vibration_description),
                    isChecked = uiState.isVibrationEnabled,
                    onCheckedChange = onVibrationEnabledChange
                )

                SettingsItemDivider()

                BubbleSettingItem(
                    title = stringResource(R.string.autohide_title),
                    description = stringResource(R.string.autohide_subtitle),
                    onClick = onAutoHideClick
                )

                SettingsItemDivider()

                BubbleSettingItem(
                    title = stringResource(R.string.bubble_about_title),
                    description = stringResource(R.string.bubble_about_description),
                    onClick = onAboutBubbleClick
                )
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BubbleSettingsScreenPreview() {
    LearnWordsTrainerTheme {
        BubbleSettingsScreen(
            uiState = BubbleSettingsUiState(bubbleSize = 50f),
            onBackPressed = {},
            onBubbleEnabledChange = {},
            onBubbleSizeChange = {},
            onTransparencyChange = {},
            onVibrationEnabledChange = {},
            onAutoHideClick = {},
            onAboutBubbleClick = {}
        )
    }
}