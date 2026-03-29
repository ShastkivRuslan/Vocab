package dev.shastkiv.vocab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.appColors

    val borderColor by animateColorAsState(
        targetValue = if (checked) colors.accent else colors.cardBorder,
        animationSpec = tween(durationMillis = 400),
        label = "switchBorderColor"
    )

    val thumbColor by animateColorAsState(
        targetValue = if (checked) colors.accent else colors.accentSoft,
        animationSpec = tween(durationMillis = 350),
        label = "thumbColor"
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "thumbOffset"
    )

    val thumbScale by animateDpAsState(
        targetValue = if (checked) 18.dp else 18.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "thumbScale"
    )

    // Track (корпус switch)
    Box(
        modifier = modifier
            .width(45.dp)
            .height(26.dp)
            .background(
                color = colors.cardBackground,
                shape = RoundedCornerShape(13.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(13.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled
            ) { onCheckedChange(!checked) }
            .padding(3.dp)
    ) {
        // Thumb (кружечок) з анімацією кольору та масштабу
        Box(
            modifier = Modifier
                .size(thumbScale)
                .offset(x = thumbOffset)
                .background(
                    color = thumbColor,
                    shape = RoundedCornerShape(9.dp)
                )
        )
    }
}