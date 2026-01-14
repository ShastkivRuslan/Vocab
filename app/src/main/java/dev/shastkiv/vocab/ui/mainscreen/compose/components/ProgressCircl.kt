package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ProgressCircle(
    progress: Int,
    title: String) {
    val targetProgress = progress / 100f
    val animatedProgress = remember { Animatable(0f) }

    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

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
        modifier = Modifier.size(dimensions.statisticsCircleSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.statisticsCircleSize)
                .background(
                    color = colors.progressGlowColor,
                    shape = CircleShape
                )
        )

        Canvas(
            modifier = Modifier.size(dimensions.statisticsCircleSize)
        ) {
            val radius = dimensions.progressCircleRadius.toPx()
            val strokeWidth = dimensions.progressCircleStrokeWidth.toPx()

            val centerX = size.width / 2
            val centerY = size.height / 2

            drawArc(
                color = colors.progressBackgroundCircleColor,
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
                        colors.progressGradientStart,
                        colors.progressGradientEnd
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
                style = dimensions.progressPercentStyle,
                color = colors.progressCenterTextTitle
            )
            Text(
                text = title,
                style = dimensions.progressLabelStyle,
                color = colors.progressCenterTextSubtitle
            )
        }
    }
}