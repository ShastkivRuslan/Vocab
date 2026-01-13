package dev.shastkiv.vocab.ui.addword.compose.components.common

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.launch

@Composable
fun ExpandableCard(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    title: String,
    showArrow: Boolean,
    isLocked: Boolean = false,
    content: @Composable () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    val bgModifier = if (isExpanded) {
        Modifier.background(colors.expandableCardExpandedBackground)
    } else {
        Modifier.background(colors.expandableCardBackground)
    }

    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(isLocked) {
                if (isLocked) return@pointerInput
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown()
                        scope.launch {
                            scale.animateTo(
                                targetValue = 0.96f,
                                animationSpec = tween(150)
                            )
                        }
                        val up = waitForUpOrCancellation()
                        scope.launch {
                            scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring()
                            )
                            if (up != null) {
                                onToggle()
                            }
                        }
                    }
                }
            }
            .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
            .then(bgModifier)
            .border(
                width = 0.8.dp,
                brush = colors.expandableCardBorder,
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            )
    ) {
        Column(modifier = Modifier.padding(dimensions.mediumPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = typography.cardTitleMedium,
                    modifier = Modifier.weight(1f),
                    color = if (isLocked) colors.textSecondary else colors.textMain
                )

                if (isLocked) {
                    ProBadge()
                } else if (showArrow) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isExpanded) colors.expandableCardArrowTint else colors.textSecondary.copy(alpha = 0.5f)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(300)),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut(animationSpec = tween(200))
            ) {
                content()
            }
        }
    }
}