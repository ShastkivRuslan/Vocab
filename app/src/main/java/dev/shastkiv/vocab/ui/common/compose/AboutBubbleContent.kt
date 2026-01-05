package dev.shastkiv.vocab.ui.common.compose

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.theme.customColors
import dev.shastkiv.vocab.ui.theme.dimensions

@Composable
fun AboutBubbleContent() {
    val dimensions = MaterialTheme.dimensions
    Column {
        LiquidGlassCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.mediumPadding)
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSizeExtraLarge)
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
                        modifier = Modifier.size(dimensions.iconSizeLarge)
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                Text(
                    text = stringResource(R.string.overlay_feature_efficiency_title),
                    style = dimensions.cardTitleStyle,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.customColors.cardTitleText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                Text(
                    text = stringResource(R.string.overlay_feature_efficiency_description),
                    style = dimensions.cartDescriptionStyle,
                    color = MaterialTheme.customColors.cardDescriptionText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spacingLarge))

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
    val dimensions = MaterialTheme.dimensions
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.extraSmallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(dimensions.iconSizeLarge),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Column {
            Text(
                text = title,
                style = dimensions.subHeaderTextStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = dimensions.cartDescriptionStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}