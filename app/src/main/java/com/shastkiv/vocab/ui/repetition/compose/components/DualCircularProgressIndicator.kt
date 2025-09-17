package com.shastkiv.vocab.ui.repetition.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.shastkiv.vocab.ui.theme.GreenSuccess
import com.shastkiv.vocab.ui.theme.RedError

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
            // Фоновий круг з легким градієнтом
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.3f),
                        backgroundColor.copy(alpha = 0.6f),
                        backgroundColor.copy(alpha = 0.3f)
                    )
                ),
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
        // Фоновий круг з тінню
        drawArc(
            color = backgroundColor.copy(alpha = 0.2f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth + 12, cap = StrokeCap.Round)
        )

        // Основний фоновий круг
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    backgroundColor.copy(alpha = 0.4f),
                    backgroundColor.copy(alpha = 0.7f),
                    backgroundColor.copy(alpha = 0.4f)
                )
            ),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth + 6, cap = StrokeCap.Round)
        )

        // Неправильні відповіді з градієнтом
        if (wrongSweepAngle > 0) {
            val wrongColorLight = Color(
                ColorUtils.blendARGB(
                    wrongColor.toArgb(),
                    Color.White.toArgb(),
                    0.3f
                )
            )

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        wrongColor,
                        wrongColorLight,
                        wrongColor
                    )
                ),
                startAngle = -90f,
                sweepAngle = wrongSweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Правильні відповіді з градієнтом
        if (correctSweepAngle > 0) {
            val correctColorLight = Color(
                ColorUtils.blendARGB(
                    correctColor.toArgb(),
                    Color.White.toArgb(),
                    0.3f
                )
            )

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        correctColor,
                        correctColorLight,
                        correctColor
                    )
                ),
                startAngle = -90f + wrongSweepAngle,
                sweepAngle = correctSweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Додаткові блики для об'ємного ефекту
        drawHighlights(correctCount, wrongCount, total, correctColor, wrongColor, strokeWidth)
    }
}

private fun DrawScope.drawHighlights(
    correctCount: Int,
    wrongCount: Int,
    total: Int,
    correctColor: Color,
    wrongColor: Color,
    strokeWidth: Float
) {
    val correctSweepAngle = (correctCount.toFloat() / total) * 360f
    val wrongSweepAngle = (wrongCount.toFloat() / total) * 360f

    // Блик на неправильних відповідях
    if (wrongSweepAngle > 30) {
        drawArc(
            color = Color.White.copy(alpha = 0.3f),
            startAngle = -90f + 10f,
            sweepAngle = 20f,
            useCenter = false,
            style = Stroke(width = strokeWidth / 3, cap = StrokeCap.Round)
        )
    }

    // Блик на правильних відповідях
    if (correctSweepAngle > 30) {
        drawArc(
            color = Color.White.copy(alpha = 0.4f),
            startAngle = -90f + wrongSweepAngle + 10f,
            sweepAngle = 20f,
            useCenter = false,
            style = Stroke(width = strokeWidth / 3, cap = StrokeCap.Round)
        )
    }
}