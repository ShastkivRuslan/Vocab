package com.shastkiv.vocab.ui.common.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.components.LiquidGlassCard
import com.shastkiv.vocab.ui.theme.customColors

@Composable
fun AboutBubbleContent() {
    Column {
        LiquidGlassCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = MaterialTheme.customColors.cardBackground,
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.customColors.cardBorder,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Bubble",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.overlay_feature_efficiency_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.customColors.cardTitleText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.overlay_feature_efficiency_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.customColors.cardDescriptionText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        InfoPoint(
            icon = Icons.Default.AutoMode,
            title = stringResource(R.string.overlay_point_social_title),
            description = stringResource(R.string.overlay_point_social_description)
        )
        InfoPoint(
            icon = Icons.Default.AllInclusive,
            title = stringResource(R.string.overlay_point_no_switch_title),
            description = stringResource(R.string.overlay_point_no_switch_description)
        )
        InfoPoint(
            icon = Icons.Default.Translate,
            title = stringResource(R.string.overlay_point_context_title),
            description = stringResource(R.string.overlay_point_context_description)
        )
        InfoPoint(
            icon = Icons.Default.Psychology,
            title = stringResource(R.string.overlay_point_ai_title),
            description = stringResource(R.string.overlay_point_ai_description)
        )
        InfoPoint(
            icon = Icons.Default.AdsClick,
            title = stringResource(R.string.overlay_point_always_near_title),
            description = stringResource(R.string.overlay_point_always_near_description)
        )
        InfoPoint(
            icon = Icons.Default.HistoryEdu,
            title = stringResource(R.string.overlay_point_no_loss_title),
            description = stringResource(R.string.overlay_point_no_loss_description)
        )
    }
}

@Composable
private fun InfoPoint(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}