package dev.shastkiv.vocab.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun SettingsSlider(
    title: String,
    description: String,
    currentValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChangeFinished: (Float) -> Unit
) {
    var sliderPosition by remember(currentValue) { mutableFloatStateOf(currentValue) }

    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    Column(modifier = Modifier.padding(dimensions.mediumPadding)) {
        Text(
            text = title,
            style = typography.cardTitleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.cardTitleText)

        Text(
            text = description,
            style = typography.cardDescriptionSmall,
            color = colors.cardDescriptionText)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensions.smallPadding),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = valueRange.start.toInt().toString(),
                color = colors.cardDescriptionText)
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = { onValueChangeFinished(sliderPosition) },
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                ,
                colors = SliderDefaults.colors(
                    thumbColor = colors.accent,
                    activeTrackColor = colors.accent,
                    inactiveTrackColor = colors.accentSoft
                )
            )
            Text(text = valueRange.endInclusive.toInt().toString(),
                color = colors.cardDescriptionText)
        }
    }
}