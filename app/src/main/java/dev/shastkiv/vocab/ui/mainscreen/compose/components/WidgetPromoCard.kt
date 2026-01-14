package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun WidgetPromoCard(onClick: () -> Unit) {
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    Box(
        modifier = Modifier
            .padding(
                dimensions.mediumPadding)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colors.accentCardGradientStart,
                            colors.accentCardGradientToEnd
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = colors.cardBorder,
                    shape = RoundedCornerShape(dimensions.mediumCornerRadius)
                )
                .clickable { onClick() }
                .padding(dimensions.mediumPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing)
            ) {
                Box(
                    modifier = Modifier
                        .size(dimensions.cardIconBoxSize)
                        .border(
                            width = 1.dp,
                            color = colors.cardBorder,
                            shape = RoundedCornerShape(dimensions.smallCornerRadius)
                        )
                        .background(
                            color = colors.accentCardIconBoxColor,
                            shape = RoundedCornerShape(dimensions.smallCornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = colors.cardTitleText
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.widget_promo_title),
                        style = typography.cardTitleMedium,
                        color = colors.cardTitleText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.widget_promo_subtitle),
                        style = typography.cardDescriptionSmall,
                        color = colors.cardDescriptionText
                    )
                }

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = colors.cardTitleText,
                    modifier = Modifier
                        .size(dimensions.iconSizeMedium)
                        .border(1.dp, colors.cardTitleText, CircleShape)
                        .padding(4.dp)
                )
            }
        }
    }
}
