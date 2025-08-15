package com.example.learnwordstrainer.ui.repetition.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.learnwordstrainer.ui.theme.GreenSuccess
import com.example.learnwordstrainer.ui.theme.RedError

@Composable
fun DualCircularProgressIndicator(
    correctCount: Int,
    wrongCount: Int,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 12f,
    correctColor: Color = GreenSuccess,
    wrongColor: Color = RedError,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val total = correctCount + wrongCount
    if (total == 0) {
        Canvas(modifier = modifier) {
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        return
    }

    val correctSweepAngle = (correctCount.toFloat() / total) * 360f
    val wrongSweepAngle = (wrongCount.toFloat() / total) * 360f

    Canvas(modifier = modifier) {
        drawArc(
            color = backgroundColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth+10, cap = StrokeCap.Round)
        )

        drawArc(
            color = wrongColor,
            startAngle = -90f,
            sweepAngle = wrongSweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawArc(
            color = correctColor,
            startAngle = -90f + wrongSweepAngle,
            sweepAngle = correctSweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}