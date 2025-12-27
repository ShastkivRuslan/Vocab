package com.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun LanguageCard(
    language: Language,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(enabled = enabled) { onClick() }
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.6f)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.customColors.cardBackground,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.customColors.cardBorder ,
                shape = MaterialTheme.shapes.medium
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = language.flagEmoji,
                    fontSize = 24.sp
                )

                Text(
                    text = language.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.customColors.cardTitleText
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }