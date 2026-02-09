package dev.shastkiv.vocab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.domain.model.enums.MasteryLevel
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun MasteryBadge(score: Int) {
    val typography = MaterialTheme.appTypography
    val corner = MaterialTheme.appDimensions.extraSmallCornerRadius
    val colors = MaterialTheme.appColors

    val level = remember(score) {
        MasteryLevel.fromScore(
            score = score,
            startColor = colors.masteryBarGradientStart,
            endColor = colors.masteryBarGradientEnd
        )
    }

    val icon = "🧠"

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(corner))
            .background(
                color = level.color.copy(alpha = 0.40f),
                shape = RoundedCornerShape(corner)
            )
            .border(
                width = 1.dp,
                color = level.color.copy(alpha = 0.7f),
                shape = RoundedCornerShape(corner)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$score",
            style = typography.cardDescriptionSmall,
            color = colors.textMain,
        )
        Text(
            text = icon,
            fontSize = 12.sp
        )
    }
}