import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme
import com.shastkiv.vocab.ui.bubble.BubbleSettingsUiState

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
                    contentDescription = stringResource(R.string.back_button_description),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                text = stringResource(R.string.bubble_settings_screen_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Text(
            modifier = Modifier.padding(start = 26.dp),
            text = stringResource(R.string.bubble_settings_screen_subtitle),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(size = 20.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                BubbleSettingItemWithSwitch(
                    title = stringResource(R.string.bubble_enable_title),
                    description = stringResource(R.string.bubble_enable_description),
                    isChecked = uiState.isBubbleEnabled,
                    onCheckedChange = onBubbleEnabledChange
                )
                Divider()
                BubbleSettingsSlider(
                    title = stringResource(R.string.bubble_size_title),
                    description = stringResource(R.string.bubble_size_description),
                    currentValue = uiState.bubbleSize,
                    valueRange = 30f..80f,
                    steps = 9,
                    onValueChangeFinished = { onBubbleSizeChange(it) }
                )
                Divider()
                BubbleSettingsSlider(
                    title = stringResource(R.string.bubble_transparency_title),
                    description = stringResource(R.string.bubble_transparency_description),
                    currentValue = uiState.bubbleTransparency,
                    valueRange = 0f..100f,
                    steps = 10,
                    onValueChangeFinished = { onTransparencyChange(it) }
                )
                Divider()
                BubbleSettingItemWithSwitch(
                    title = stringResource(R.string.bubble_vibration_title),
                    description = stringResource(R.string.bubble_vibration_description),
                    isChecked = uiState.isVibrationEnabled,
                    onCheckedChange = onVibrationEnabledChange
                )
                Divider()
                BubbleSettingItem(
                    title = stringResource(R.string.bubble_autohide_title),
                    description = stringResource(R.string.bubble_autohide_description),
                    onClick = onAutoHideClick
                )
                Divider()
                BubbleSettingItem(
                    title = stringResource(R.string.bubble_about_title),
                    description = stringResource(R.string.bubble_about_description),
                    onClick = onAboutBubbleClick
                )
            }
        }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@Composable
fun BubbleSettingItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_right_chevron),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun BubbleSettingItemWithSwitch(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = if (title == "Плаваюча бульбашка") 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Switch(
            modifier = Modifier
                .scale(0.8f),
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun BubbleSettingsSlider(
    title: String,
    description: String,
    currentValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChangeFinished: (Float) -> Unit
) {
    var sliderPosition by remember(currentValue) { mutableFloatStateOf(currentValue) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.padding(top = 4.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = valueRange.start.toInt().toString())
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = { onValueChangeFinished(sliderPosition) },
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )
            Text(text = valueRange.endInclusive.toInt().toString())
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