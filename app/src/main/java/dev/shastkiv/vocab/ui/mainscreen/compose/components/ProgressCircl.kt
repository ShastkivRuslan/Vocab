package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.customColors
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ProgressCircle(progress: Int) {
    val customColors = MaterialTheme.customColors
    val targetProgress = progress / 100f

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(targetProgress) {
        if (animatedProgress.value == 0f && targetProgress > 0f) {

            delay(500)

            animatedProgress.animateTo(
                targetValue = targetProgress,
                animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
            )
        } else {
            animatedProgress.animateTo(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier.size(112.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .background(
                    color = customColors.progressGlowColor,
                    shape = CircleShape
                )
                .blur(24.dp)
        )

        Canvas(
            modifier = Modifier.size(112.dp)
        ) {
            val radius = 45.dp.toPx()
            val centerX = size.width / 2
            val centerY = size.height / 2
            val strokeWidth = 6.dp.toPx()

            drawArc(
                color = customColors.progressBackgroundCircleColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )

            val progressSweepAngle = 360f * animatedProgress.value
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        customColors.progressGradientStart,
                        customColors.progressGradientEnd
                    )
                ),
                startAngle = -90f,
                sweepAngle = progressSweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(animatedProgress.value * 100).roundToInt()}%",
                style = MaterialTheme.typography.titleLarge,
                color = customColors.progressCenterTextTitle
            )
            Text(
                text = stringResource(R.string.learned),
                style = MaterialTheme.typography.bodySmall,
                color = customColors.progressCenterTextSubtitle
            )
        }
    }
}