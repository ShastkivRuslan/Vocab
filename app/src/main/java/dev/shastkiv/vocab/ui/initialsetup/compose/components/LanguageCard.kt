package dev.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.ui.theme.customColors
import dev.shastkiv.vocab.ui.theme.dimensions

@Composable
fun LanguageCard(
    language: Language,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val dimensions = MaterialTheme.dimensions
    val customColors = MaterialTheme.customColors
    val defaultColors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .clickable(enabled = enabled) { onClick() }
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.6f)
            .clip(RoundedCornerShape(dimensions.cornerRadius))
            .background(
                color = customColors.cardBackground,
                shape = RoundedCornerShape(dimensions.cornerRadius)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) defaultColors.primary else customColors.cardBorder,
                shape = RoundedCornerShape(dimensions.cornerRadius)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.largePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            Text(
                text = language.flagEmoji,
                fontSize = dimensions.emojiSize
            )

            Text(
                text = language.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier.weight(1f),
                color = customColors.cardTitleText
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = defaultColors.primary
                )
            }
        }
    }
}