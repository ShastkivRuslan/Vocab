package dev.shastkiv.vocab.ui.addword.compose.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun LevelBadge(level: String) {
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors
    Text(
        text = level,
        style = typography.cardTitleMedium,
        color = colors.accent,
        modifier = Modifier
            .background(
                colors.accentSoft,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}