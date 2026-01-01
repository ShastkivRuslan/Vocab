package dev.example.languageapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.mainscreen.compose.components.ProgressCircle
import dev.shastkiv.vocab.ui.theme.customColors

data class StatItem(
    val label: String,
    val value: Int
)

@Composable
fun UserStatsCard(
    totalWordsCount: Int,
    notLearnedWordsCount: Int,
    todayWordsCount: Int,
    learnedPercentage: Int) {
    val stats = listOf(
        StatItem(stringResource(R.string.count_words), totalWordsCount),
        StatItem(stringResource(R.string.words_to_learn), notLearnedWordsCount),
        StatItem(stringResource(R.string.added_today), todayWordsCount)
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
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
            ProgressCircle(progress = learnedPercentage)

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
                            text = stat.value.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.customColors.statsValueText
                        )
                    }
                }
            }
        }
    }
}