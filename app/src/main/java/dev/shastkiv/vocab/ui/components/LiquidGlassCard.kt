package dev.shastkiv.vocab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
            .background(
                color = MaterialTheme.appColors.cardBackground,
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.appColors.cardBorder,
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            )
    ) {
        content()
    }
}