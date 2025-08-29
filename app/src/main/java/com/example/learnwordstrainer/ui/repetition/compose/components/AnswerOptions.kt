package com.example.learnwordstrainer.ui.repetition.compose.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.ui.theme.GreenSuccess
import com.example.learnwordstrainer.ui.theme.RedError

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

            val backgroundColor = when {
                isSelected && isAnswerCorrect == false -> RedError.copy(alpha = 0.2f)
                isSelected && isAnswerCorrect == true -> GreenSuccess.copy(alpha = 0.2f)
                isAnswerCorrect == true -> GreenSuccess.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val numberBackgroundColor = when {
                isSelected && isAnswerCorrect == false -> RedError
                isSelected && isAnswerCorrect == true -> GreenSuccess
                isCorrect && isAnswerCorrect != null -> GreenSuccess
                else -> MaterialTheme.colorScheme.surface
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(enabled = selectedAnswerIndex == null) {
                        onAnswerClick(index)
                    },
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
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
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}