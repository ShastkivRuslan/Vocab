package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    wrongCount: Int,
    correctCount: Int
) {
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    val total = wrongCount + correctCount
    val correctPercentage = if (total == 0) 0 else ((correctCount.toFloat() / total) * 100).toInt()

    val stats = listOf(
        ProgressStatItem(
            label = stringResource(R.string.correct_answers),
            value = correctCount.toString()
        ),
        ProgressStatItem(
            label = stringResource(R.string.wrong_answers),
            value = wrongCount.toString()
        ),
        ProgressStatItem(
            label = stringResource(R.string.total_attempts),
            value = total.toString()
        )
    )

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
            .padding(dimensions.mediumPadding)
    ) {
        Column(
        ) {
            Text(
                text = stringResource(R.string.daily_statistics),
                style = typography.cardTitleMedium,
                color = colors.textMain,
                modifier = Modifier.padding(bottom = dimensions.extraSmallPadding)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box() {
                    ProgressCircle(
                        progress = correctPercentage,
                        title = stringResource(R.string.accuracy), // Вже було, залишаємо
                    )
                }

                Spacer(modifier = Modifier.width(dimensions.largeSpacing))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing)
                ) {
                    stats.forEach { stat ->
                        Row {
                            Text(
                                text = stat.label,
                                style = typography.cardDescriptionSmall,
                                color = colors.statsLabelText
                            )
                            Spacer(modifier = Modifier.weight(1f))
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
}