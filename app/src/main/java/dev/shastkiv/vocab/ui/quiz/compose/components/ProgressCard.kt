package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.mainscreen.compose.components.ProgressCircle
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

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

    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.largeCornerRadius))
            .background(
                color = colors.cardBackground,
                shape = RoundedCornerShape(dimensions.largeCornerRadius)
            )
            .border(
                width = 1.dp,
                color = colors.cardBorder,
                shape = RoundedCornerShape(dimensions.largeCornerRadius)
            )
            .padding(dimensions.largePadding)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colors.statsGlowStartColor,
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(dimensions.largeCornerRadius)
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressCircle(
                progress = correctPercentage,
                title = stringResource(R.string.accuracy)
            )

            Spacer(modifier = Modifier.width(dimensions.largeSpacing))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing)
            ) {
                stats.forEach { stat ->
                    Column {
                        Text(
                            text = stat.label,
                            style = typography.cardDescriptionSmall,
                            color = colors.statsLabelText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stat.value,
                            style = typography.cardDescriptionSmall,
                            color = colors.statsValueText
                        )
                    }
                }
            }
        }
    }
}