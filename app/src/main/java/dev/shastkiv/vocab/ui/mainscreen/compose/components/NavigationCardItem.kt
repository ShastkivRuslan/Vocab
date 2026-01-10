package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

data class NavigationCardData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val colorGradient: List<Color>,
    val iconBgColor: Color,
    val iconColor: Color
)

@Composable
fun NavigationCardItem(
    card: NavigationCardData,
    onClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
            .background(
                color = colors.cardBackground,
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            )
            .border(
                width = 1.dp,
                color = colors.cardBorder,
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            )
            .clickable {
                onClick()
            }
            .padding(dimensions.mediumPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.cardIconBoxSize)
                    .clip(RoundedCornerShape(dimensions.smallCornerRadius))
                    .background(
                        color = card.iconBgColor,
                        shape = RoundedCornerShape(dimensions.smallCornerRadius)
                    )
                    .border(
                        width = 1.dp,
                        color = colors.cardBorder,
                        shape = RoundedCornerShape(dimensions.smallCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = card.title,
                    tint = colors.cardIconTintLight,
                    modifier = Modifier
                        .size(dimensions.iconSizeMedium)
                )
            }

            Spacer(modifier = Modifier.width(dimensions.mediumSpacing))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = card.title,
                    style = typography.cardTitleMedium,
                    color = colors.cardTitleText
                )
                Spacer(modifier = Modifier.height(dimensions.extraSmallSpacing))
                Text(
                    text = card.description,
                    style = typography.cardDescriptionSmall,
                    color = colors.cardDescriptionText
                )
            }

            Box(
                modifier = Modifier
                    .size(dimensions.cardArrowBoxSize)
                    .clip(RoundedCornerShape(dimensions.extraSmallCornerRadius))
                    .background(
                        color = colors.cardArrowBackground,
                        shape = RoundedCornerShape(dimensions.extraSmallCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = colors.cardArrowTint,
                    modifier = Modifier.size(dimensions.cardArrowIconSize)
                )
            }
        }
    }
}