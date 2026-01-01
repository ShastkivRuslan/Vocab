package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.mainscreen.compose.components.ProgressCircle
import dev.shastkiv.vocab.ui.theme.customColors

data class ProgressStatItem(
    val label: String,
    val value: String
)

@Composable
fun ProgressCard(
    correctCount: Int,
    wrongCount: Int
) {
    val total = correctCount + wrongCount
    val correctPercentage = if (total > 0) ((correctCount.toFloat() / total) * 100).toInt() else 0

    val stats = listOf(
        ProgressStatItem("Правильні відповіді", correctCount.toString()),
        ProgressStatItem("Неправильні відповіді", wrongCount.toString()),
        ProgressStatItem("Усього спроб", total.toString())
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                color = MaterialTheme.customColors.cardBackground,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.customColors.cardBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.customColors.statsGlowStartColor,
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressCircle(progress = correctPercentage)

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                stats.forEach { stat ->
                    Column {
                        Text(
                            text = stat.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.customColors.statsLabelText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stat.value,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.customColors.statsValueText
                        )
                    }
                }
            }
        }
    }
}