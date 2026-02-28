package dev.shastkiv.vocab.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.shastkiv.vocab.ui.components.CustomSwitch
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun SettingItemWithSwitch(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(dimensions.mediumPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = typography.cardTitleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.cardTitleText
            )

            Text(
                text = description,
                style = typography.cardDescriptionSmall,
                color = colors.cardDescriptionText
            )
        }

        CustomSwitch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}