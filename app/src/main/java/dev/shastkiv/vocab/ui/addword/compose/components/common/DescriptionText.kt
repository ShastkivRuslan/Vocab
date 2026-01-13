package dev.shastkiv.vocab.ui.addword.compose.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun DescriptionText(text: String) {
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    Text(
        text = text,
        style = typography.cardDescriptionSmall,
        textAlign = TextAlign.Center,
        color = colors.textSecondary
    )
}