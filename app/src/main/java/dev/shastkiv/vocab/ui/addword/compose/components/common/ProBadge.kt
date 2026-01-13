package dev.shastkiv.vocab.ui.addword.compose.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun ProBadge() {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    Text(
        text = "PRO",
        style = typography.cardTitleMedium.copy(fontSize = 10.sp), // Стиль з системи + мікро-корекція
        color = colors.onAccent,
        modifier = Modifier
            .background(
                color = colors.accent.copy(alpha = 0.8f),
                shape = RoundedCornerShape(dimensions.smallCornerRadius)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}