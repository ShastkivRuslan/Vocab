package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun ScoreItem(count: Int, label: String, color: Color, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = typography.wordHeadLine,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = typography.cardDescriptionSmall,
            color = colors.cardTitleText
        )
    }
}