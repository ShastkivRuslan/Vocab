package dev.shastkiv.vocab.ui.addword.compose.components.sections

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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.ui.addword.compose.components.common.ExpandableCard
import dev.shastkiv.vocab.ui.addword.compose.components.common.LevelBadge
import dev.shastkiv.vocab.ui.addword.compose.components.common.ProFeatureLock
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

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
        title = stringResource(R.string.main_info),
        showArrow = !isLocked,
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
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    Column {
        Spacer(modifier = Modifier.height(dimensions.smallSpacing))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = originalWord,
                style = typography.wordHeadLine,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.width(dimensions.smallSpacing))

            if(!isLocked) {
                LevelBadge(level = wordData?.level ?: "[...]")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (!isLocked) {
            Text(
                text = "[ ${wordData?.transcription ?: "..."} ]",
                style = typography.cardTitleMedium,
                color = colors.cardDescriptionText
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.translation),
            style = typography.cardTitleMedium,
            fontWeight = FontWeight.Medium,
            color = colors.textMain
        )

        Spacer(modifier = Modifier.height(dimensions.extraSmallSpacing))

        Text(
            text = translation ?: "...",
            style = typography.wordHeadLine,
            fontWeight = FontWeight.Bold,
            color = colors.accent
        )

        Spacer(modifier = Modifier.height(dimensions.extraSmallSpacing))

        if (!isLocked) {
            Text(
                text = wordData?.partOfSpeech ?: "...",
                style = typography.cardTitleMedium,
                color = colors.cardDescriptionText,
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