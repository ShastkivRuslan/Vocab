package dev.shastkiv.vocab.ui.addword.compose.components.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlin.math.sin

@Composable
fun WordInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClear: () -> Unit,
    readOnly: Boolean = false,
    isListening: Boolean = false,
    // isReady = мікрофон активно слухає (після onReadyForSpeech)
    // isListening && !isReady = проміжок ініціалізації або обробки після мовлення
    isReady: Boolean = false,
    rmsLevel: Float = 0f,
    onMicClick: (() -> Unit)? = null
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val accentColor = MaterialTheme.colorScheme.primary

    val isActive = isListening
    // Спіннер коли запущено але ще не готово, або вже закінчив говорити (обробка)
    val isProcessing = isListening && !isReady

    val targetScale = if (isReady) {
        val normalized = ((rmsLevel + 2f) / 12f).coerceIn(0f, 1f)
        1f + normalized * 0.45f
    } else 1f

    val micScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "micScale"
    )

    OutlinedTextField(
        value = if (isActive) TextFieldValue("") else value,
        onValueChange = if (isActive) ({}) else onValueChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = typography.cardTitleMedium,
        label = if (!isActive) {
            { Text(text = stringResource(R.string.origin_word), style = typography.cardDescriptionMedium) }
        } else null,
        singleLine = true,
        readOnly = readOnly || isActive,
        shape = RoundedCornerShape(dimensions.mediumCornerRadius),
        colors = if (isActive) OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = accentColor.copy(alpha = 0.6f)
        ) else OutlinedTextFieldDefaults.colors(),
        prefix = if (isActive) {
            {
                AnimatedContent(
                    targetState = isProcessing,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                    label = "sttContent"
                ) { processing ->
                    Box(
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (processing) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = accentColor
                                )
                                Text(
                                    text = "Обробляю...",
                                    style = typography.cardDescriptionMedium,
                                    color = colors.textSecondary
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                VoiceWaveform(
                                    rmsLevel = rmsLevel,
                                    color = accentColor,
                                    modifier = Modifier.size(width = 48.dp, height = 20.dp)
                                )
                                Text(
                                    text = "Говоріть...",
                                    style = typography.cardDescriptionMedium,
                                    color = accentColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        } else null,
        trailingIcon = {
            when {
                isProcessing -> { /* спіннер у prefix */ }
                isListening -> {
                    IconButton(onClick = { onMicClick?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Stop",
                            tint = accentColor,
                            modifier = Modifier.scale(micScale)
                        )
                    }
                }
                value.text.isNotEmpty() && !readOnly -> {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = colors.textSecondary
                        )
                    }
                }
                onMicClick != null && !readOnly -> {
                    IconButton(onClick = { onMicClick() }) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Speak",
                            tint = colors.textSecondary
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun VoiceWaveform(
    rmsLevel: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val amplitude by animateFloatAsState(
        targetValue = ((rmsLevel + 2f) / 12f).coerceIn(0.1f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "amplitude"
    )

    Canvas(modifier = modifier) {
        val barCount = 5
        val barWidth = size.width / (barCount * 2 - 1)
        val maxBarHeight = size.height

        for (i in 0 until barCount) {
            val x = i * barWidth * 2 + barWidth / 2
            val sinValue = sin(phase + i * 1.2f)
            val barHeight = (maxBarHeight * amplitude * ((sinValue + 1f) / 2f))
                .coerceAtLeast(maxBarHeight * 0.15f)
            val top = (size.height - barHeight) / 2f

            drawRoundRect(
                color = color.copy(alpha = 0.85f),
                topLeft = Offset(x, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2)
            )
        }
    }
}