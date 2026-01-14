package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnswerOptions(
    options: List<String>,
    selectedAnswerIndex: Int?,
    correctAnswerIndex: Int,
    isAnswerCorrect: Boolean?,
    onAnswerClick: (Int) -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val roundedCornerShape = RoundedCornerShape(dimensions.largeCornerRadius)

    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    ) {
        options.forEachIndexed { index, text ->
            val isSelected = selectedAnswerIndex == index
            val isCorrect = correctAnswerIndex == index
            val hasAnswered = selectedAnswerIndex != null

            val isVisible = if (!hasAnswered) {
                true
            } else {
                isCorrect || isSelected
            }
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(500))
            ) {
                val animatedOffsetY = remember { Animatable(- 100f)}
                val animatedAlpha = remember { Animatable(0f) }
                val animatedScale = remember { Animatable(0.8f)}

                LaunchedEffect(text) {
                    animatedOffsetY.snapTo(- 100f)
                    animatedAlpha.snapTo(0f)
                    animatedScale.snapTo(0.8f)

                    delay(index * 100L)

                    launch { animatedOffsetY.animateTo(0f,tween(300)) }
                    launch {
                        animatedAlpha.animateTo(
                            1f,
                            tween(300)
                        )
                    }
                    launch { animatedScale.animateTo(1f,tween(300)) }
                }

                val targetBgColor by animateColorAsState(
                    targetValue = when {
                        isSelected && isAnswerCorrect == false -> colors.redError.copy(alpha = 0.25f)
                        (isCorrect || isSelected) && isAnswerCorrect == true -> colors.greenSuccess.copy(
                            alpha = 0.25f
                        )

                        isCorrect && hasAnswered -> colors.greenSuccess.copy(alpha = 0.20f)
                        else -> colors.cardBackground
                    },
                    animationSpec = tween(500),
                    label = "bg_color"
                )

                val targetBorderColor by animateColorAsState(
                    targetValue = when {
                        isSelected && isAnswerCorrect == false -> colors.redError
                        (isCorrect || isSelected) && isAnswerCorrect == true -> colors.greenSuccess
                        isCorrect && hasAnswered -> colors.greenSuccess
                        else -> colors.cardBorder
                    },
                    animationSpec = tween(500),
                    label = "border_color"
                )

                Box(
                    modifier = Modifier
                        .padding(bottom = dimensions.smallSpacing)
                        .fillMaxWidth()
                        .clip(roundedCornerShape)
                        .graphicsLayer {
                            translationY = animatedOffsetY.value
                            alpha = animatedAlpha.value
                            scaleX = animatedScale.value
                            scaleY = animatedScale.value
                        }
                        .background(
                            brush = if (hasAnswered) {
                                Brush.verticalGradient(
                                    colors = listOf(targetBgColor, Color.Transparent),
                                    endY = 300f
                                )
                            } else {
                                SolidColor(colors.cardBackground)
                            },
                            shape = roundedCornerShape
                        )
                        .clickable(enabled = !hasAnswered) { onAnswerClick(index) }
                        .border(
                            width = 1.dp,
                            brush = if (hasAnswered) {
                                Brush.verticalGradient(
                                    colors = listOf(targetBorderColor, Color.Transparent),
                                    endY = 200f
                                )
                            } else {
                                SolidColor(colors.cardBorder)
                            },
                            shape = roundedCornerShape
                        )
                        .drawBehind {
                            if (hasAnswered && (isSelected || isCorrect)) {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            targetBorderColor.copy(alpha = 0.15f),
                                            Color.Transparent
                                        ),
                                        center = Offset(size.width / 2, 0f),
                                        radius = size.width
                                    )
                                )
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(dimensions.smallPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(dimensions.cardArrowBoxSize)
                                .clip(RoundedCornerShape(dimensions.smallCornerRadius))
                                .background(colors.expandableCardBackground)
                                .border(
                                    width = 1.dp,
                                    brush = if (hasAnswered) {
                                        Brush.verticalGradient(
                                            colors = listOf(targetBorderColor, Color.Transparent),
                                            endY = 200f
                                        )
                                    } else {
                                        SolidColor(colors.cardBorder)
                                    },
                                    shape = RoundedCornerShape(dimensions.smallCornerRadius)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = typography.cardTitleMedium,
                                color = if (hasAnswered && (isCorrect || isSelected)) targetBorderColor else colors.cardTitleText
                            )
                        }

                        Spacer(modifier = Modifier.width(dimensions.smallSpacing))

                        Text(
                            text = text,
                            style = typography.cardTitleMedium,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = colors.cardTitleText
                        )
                    }
                }
            }
        }
    }
}
