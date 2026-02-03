package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun CompactCategoryCard(
    titleRes: Int,
    descriptionRes: Int,
    count: Int,
    isSelected: Boolean,
    onCardClick: () -> Unit
) {
    val colors = MaterialTheme.appColors
    val dims = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    val isEnabled = count > 0

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(count) {
        visible = true
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) (if (isEnabled) 1f else 0.5f) else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(animatedAlpha)
            .clip(RoundedCornerShape(dims.largeCornerRadius))
            .background(
                color = colors.cardBackground,
                shape = RoundedCornerShape(dims.largeCornerRadius)
            )
            .border(
                width = 1.dp,
                color = if (isSelected && isEnabled) {
                    colors.accent
                } else {
                    colors.cardBorder.copy(alpha = if (isEnabled) 0.5f else 0.2f)
                },
                shape = RoundedCornerShape(dims.largeCornerRadius)
            )
            .clickable(enabled = isEnabled) { onCardClick() }
            .padding(horizontal = dims.mediumPadding, vertical = dims.smallPadding)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(titleRes),
                    style = typography.cardTitleMedium,
                    color = if (isSelected && isEnabled) {
                        colors.accent
                    } else {
                        colors.textMain.copy(alpha = if (isEnabled) 1f else 0.6f)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(dims.smallSpacing))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(dims.smallCornerRadius))
                        .background(
                            color = if (isEnabled) {
                                colors.expandableCardBackground
                            } else {
                                colors.expandableCardBackground.copy(alpha = 0.1f)
                            }
                        )
                        .border(
                            width = 1.dp,
                            brush = if (isEnabled) {
                                if (isSelected) {
                                    SolidColor(colors.accent.copy(alpha = 0.5f))
                                } else {
                                    colors.expandableCardBorder
                                }
                            } else {
                                SolidColor(colors.cardBorder.copy(alpha = 0.3f))
                            },
                            shape = RoundedCornerShape(dims.smallCornerRadius)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = count.toString(),
                        style = typography.cardDescriptionMedium,
                        color = if (isEnabled) {
                            if (isSelected) colors.accent else colors.textMain
                        } else {
                            colors.textMain.copy(alpha = 0.4f)
                        }
                    )
                    Text(
                        text = stringResource(R.string.words_count),
                        style = typography.cardDescriptionSmall.copy(
                            fontSize = 11.sp
                        ),
                        color = if (isEnabled) {
                            colors.cardDescriptionText
                        } else {
                            colors.cardDescriptionText.copy(alpha = 0.4f)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dims.smallSpacing))

            Text(
                text = if (isEnabled) {
                    stringResource(descriptionRes)
                } else {
                    stringResource(R.string.no_words_available)
                },
                style = typography.cardDescriptionSmall,
                color = colors.cardDescriptionText.copy(
                    alpha = if (isEnabled) 0.8f else 0.5f
                )
            )
        }
    }
}