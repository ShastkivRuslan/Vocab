package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun SettingsButton(onClick: () -> Unit) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    IconButton(
        onClick = onClick ,
        modifier = Modifier
            .statusBarsPadding()
            .size(dimensions.iconSizeExtraLarge)
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
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "SettingsButton",
            tint = colors.accent,
            modifier = Modifier.size(dimensions.iconSizeMedium)
        )
    }
}