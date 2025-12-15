package com.shastkiv.vocab.ui.repetition.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.ui.theme.GreenSuccess
import com.shastkiv.vocab.ui.theme.RedError
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun AnswerOptions(
    options: List<String>,
    selectedAnswerIndex: Int?,
    correctAnswerIndex: Int,
    isAnswerCorrect: Boolean?,
    onAnswerClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        options.forEachIndexed { index, text ->
            val isSelected = selectedAnswerIndex == index
            val isCorrect = correctAnswerIndex == index

            val numberBackgroundColor = when {
                isSelected && isAnswerCorrect == false -> RedError
                isSelected && isAnswerCorrect == true -> GreenSuccess
                isCorrect && isAnswerCorrect != null -> GreenSuccess
                else -> MaterialTheme.colorScheme.surface
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        color = MaterialTheme.customColors.cardBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable(enabled = selectedAnswerIndex == null) {
                        onAnswerClick(index)
                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.customColors.cardBorder,
                        shape = MaterialTheme.shapes.medium
                    ),

            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(numberBackgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "${index + 1}", color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.customColors.cardTitleText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
