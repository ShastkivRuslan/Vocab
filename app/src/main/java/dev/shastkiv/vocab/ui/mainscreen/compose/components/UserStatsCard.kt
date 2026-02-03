package dev.example.languageapp

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.mainscreen.compose.components.ProgressCircle
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

data class StatItem(
    val label: String,
    val value: Int
)

@Composable
fun UserStatsCard(
    totalWordsCount: Int,
    notLearnedWordsCount: Int,
    todayWordsCount: Int,
    learnedPercentage: Int
) {
    val stats = listOf(
        StatItem(stringResource(R.string.count_words), totalWordsCount),
        StatItem(stringResource(R.string.words_to_learn), notLearnedWordsCount),
        StatItem(stringResource(R.string.added_today), todayWordsCount)
    )
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    Box(
        modifier = Modifier
            .padding(horizontal = dimensions.mediumPadding)
            .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
            .background(
                color = colors.cardBackground,
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            )
            .border(
                width = 1.dp,
                color = colors.cardBorder,
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            )
            .padding(dimensions.mediumPadding)
    ) {
        Column {
            Text(
                text = stringResource(R.string.general_statistic),
                style = typography.cardTitleMedium,
                color = colors.textMain,
                modifier = Modifier.padding(bottom = dimensions.extraSmallPadding)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressCircle(
                    progress = learnedPercentage,
                    title = stringResource(R.string.learned))

                Spacer(modifier = Modifier.width(dimensions.largeSpacing))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing)
                ) {
                    stats.forEach { stat ->
                        Row {
                            Text(
                                text = stat.label,
                                style = dimensions.cardDescriptionSmallStyle,
                                color = colors.statsLabelText
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = stat.value.toString(),
                                style = dimensions.cartDescriptionMediumStyle,
                                color = colors.statsValueText
                            )
                        }
                    }
                }
            }

        }
    }
}
