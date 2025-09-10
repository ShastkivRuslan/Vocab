package com.shastkiv.vocab.ui.repetition.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.theme.GreenSuccess
import com.shastkiv.vocab.ui.theme.RedError

@Composable
fun WordCard(
    word: String,
    correctCount: Int,
    wrongCount: Int,
    onListenClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.translate_word),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onListenClick) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = word,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreItem(
                    count = correctCount,
                    label = stringResource(R.string.correct),
                    color = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )
                HorizontalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                )
                ScoreItem(
                    count = wrongCount,
                    label = stringResource(R.string.errors),
                    color = RedError,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}