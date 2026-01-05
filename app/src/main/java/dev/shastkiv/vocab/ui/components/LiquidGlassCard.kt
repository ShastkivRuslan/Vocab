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
import dev.shastkiv.vocab.ui.theme.customColors
import dev.shastkiv.vocab.ui.theme.dimensions

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dimensions = MaterialTheme.dimensions
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadius))
            .background(
                color = MaterialTheme.customColors.cardBackground,
                shape = RoundedCornerShape(dimensions.cornerRadius)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.customColors.cardBorder,
                shape = RoundedCornerShape(dimensions.cornerRadius)
            )
    ) {
        content()
    }
}