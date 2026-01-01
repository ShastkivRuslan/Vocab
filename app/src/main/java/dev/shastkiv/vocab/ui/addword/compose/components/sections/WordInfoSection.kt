package dev.shastkiv.vocab.ui.addword.compose.components.sections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.ui.addword.compose.components.common.*
@Composable
fun WordInfoSection(
    originalWord: String,
    translation: String?,
    wordData: WordData?,
    isExpanded: Boolean,
    isLocked: Boolean,
    onToggle: () -> Unit
) {
    ExpandableCard(
        isExpanded = isExpanded,
        onToggle = onToggle,
        title = if (isExpanded || isLocked) stringResource(R.string.main_info) else originalWord,
        showArrow = !isLocked && !isExpanded,
        isLocked = false
    ) {
        WordInfoContent(originalWord, translation, wordData, isLocked)
    }
}

@Composable
private fun WordInfoContent(
    originalWord: String,
    translation: String?,
    wordData: WordData?,
    isLocked: Boolean
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = originalWord,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.width(8.dp))

            if(!isLocked) {
                LevelBadge(level = wordData?.level ?: "[...]")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (!isLocked) {
            Text(
                text = wordData?.transcription ?: "[...]",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.translation),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = translation ?: "...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (!isLocked) {
            Text(
                text = wordData?.partOfSpeech ?: "...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (isLocked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .blur(radius = 2.dp)
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Транскрипція:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Рівень:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Частина мови:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                ProFeatureLock(modifier = Modifier.matchParentSize())
            }
        }
    }
}