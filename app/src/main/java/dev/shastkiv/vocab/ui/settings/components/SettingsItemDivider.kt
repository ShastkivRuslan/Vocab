package dev.shastkiv.vocab.ui.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun SettingsItemDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = MaterialTheme.appDimensions.smallSpacing),
        thickness = 1.dp,
        color = MaterialTheme.appColors.cardBorder
    )
}