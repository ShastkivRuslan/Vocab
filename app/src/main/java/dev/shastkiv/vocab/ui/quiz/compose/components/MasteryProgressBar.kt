package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.enums.ExerciseType
import dev.shastkiv.vocab.domain.model.enums.MasteryLevel
import dev.shastkiv.vocab.ui.components.MasteryBadge
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun MasteryProgressBar(
    masteryScore: Int,
    exerciseType: ExerciseType? = null,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val density = LocalDensity.current

    var targetProgress by remember { mutableStateOf(masteryScore / 100f) }
    var previousProgress by remember { mutableStateOf(masteryScore / 100f) }
    var showEnergyWave by remember { mutableStateOf(false) }
    var isProgressDecreasing by remember { mutableStateOf(false) }

    LaunchedEffect(masteryScore) {
        val newProgress = masteryScore / 100f
        previousProgress = targetProgress
        targetProgress = newProgress
        isProgressDecreasing = newProgress < previousProgress
        showEnergyWave = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "ProgressFill"
    )

    val animatedScore by animateIntAsState(
        targetValue = masteryScore,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ScoreAnimation"
    )

    val energyWaveOffset = remember { Animatable(0f) }
    var showInfoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(masteryScore) {
        energyWaveOffset.snapTo(0f)
        energyWaveOffset.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = if (isProgressDecreasing) 1200 else 1500,
                easing = FastOutSlowInEasing
            )
        )
        showEnergyWave = false
    }

    val infiniteTransition = rememberInfiniteTransition(label = "EnergyFlow")

    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GradientShift"
    )

    val energyPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EnergyPulse"
    )

    val ambientWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AmbientWave"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showInfoDialog = true }
                    .padding(top = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.mastery_level),
                    style = typography.cardDescriptionMedium,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = colors.textSecondary.copy(alpha = 0.7f),
                    modifier = Modifier.size(dimensions.iconSizeMedium)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            MasteryBadge(score = animatedScore)
        }

        Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.masteryProgressHeight),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(dimensions.masteryProgressHeight * 2)
                    .graphicsLayer {
                        translationY =
                            with(density) { (dimensions.masteryProgressHeight * 0.4f).toPx() }
                        alpha = (0.35f + ambientWave * 0.15f) * energyPulse
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val glowRadius = size.height * 1.5f
                    val glowColor = MasteryLevel.getEnergyColor(
                        score = masteryScore,
                        startColor = colors.masteryBarGradientStart,
                        endColor = colors.masteryBarGradientEnd
                    )

                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.7f),
                                glowColor.copy(alpha = 0.4f),
                                glowColor.copy(alpha = 0.2f),
                                glowColor.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, 0f),
                            radius = size.width.coerceAtLeast(glowRadius) // Щоб не було занадто вузьким
                        ),
                        cornerRadius = CornerRadius(glowRadius, glowRadius)
                    )
                }
            }

            val cornerRadiusPx = with(density) { dimensions.masteryProgressCornerRadius.toPx() }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(dimensions.masteryProgressCornerRadius))
                    .background(colors.cardBorder.copy(alpha = 0.1f))
                    .border(
                        width = dimensions.masteryProgressBorderWidth,
                        color = colors.cardBorder.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(dimensions.masteryProgressCornerRadius)
                    )
            ) {
                if (masteryScore != 0) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val fillW = w * animatedProgress

                        val baseColor = MasteryLevel.getEnergyColor(
                            score = masteryScore,
                            startColor = colors.masteryBarGradientStart,
                            endColor = colors.masteryBarGradientEnd)
                        val highlightColor = baseColor
                        val deepColor = MasteryLevel.getEnergyColorDeep(
                            score = masteryScore,
                            startColor = colors.masteryBarGradientStart,
                            endColor = colors.masteryBarGradientEnd
                        )
                        val ultraBright = MasteryLevel.getEnergyColorUltraBright(
                            score = masteryScore,
                            startColor = colors.masteryBarGradientStart,
                            endColor = colors.masteryBarGradientEnd
                        )

                        val shift = gradientShift * 2f

                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    deepColor,
                                    baseColor,
                                    highlightColor.copy(alpha = 1f * energyPulse),
                                    ultraBright.copy(alpha = 0.8f * energyPulse),
                                    highlightColor.copy(alpha = 1f * energyPulse),
                                    baseColor,
                                    deepColor
                                ),
                                startX = -fillW * shift,
                                endX = fillW * (2f - shift)
                            ),
                            size = size.copy(width = fillW),
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.25f * energyPulse),
                                    Color.Transparent,
                                    deepColor.copy(alpha = 0.3f)
                                )
                            ),
                            size = size.copy(width = fillW),
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.18f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f)
                                )
                            )
                        )
                )

                if (showEnergyWave) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        val fillW = w * animatedProgress
                        val previousFillW = w * previousProgress

                        val waveX = if (isProgressDecreasing) {
                            previousFillW - (previousFillW - fillW) * energyWaveOffset.value
                        } else {
                            previousFillW + (fillW - previousFillW) * energyWaveOffset.value
                        }

                        val waveY = h / 2f

                        val fadeProgress = energyWaveOffset.value
                        val fadeIn = if (fadeProgress < 0.2f) fadeProgress / 0.2f else 1f
                        val fadeOut =
                            if (fadeProgress > 0.7f) 1f - ((fadeProgress - 0.7f) / 0.3f) else 1f
                        val waveAlpha = fadeIn * fadeOut

                        val waveColor = if (isProgressDecreasing) {
                            colors.masteryProgressDecreaseWave
                        } else {
                            MasteryLevel.getEnergyColorUltraBright(
                                score = masteryScore,
                                startColor = colors.masteryBarGradientStart,
                                endColor = colors.masteryBarGradientEnd
                            )
                        }

                        val outerRadius = h * 4.0f
                        val coreRadius = h * 1.7f

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    waveColor.copy(alpha = 0.8f * waveAlpha),
                                    waveColor.copy(alpha = 0.6f * waveAlpha),
                                    waveColor.copy(alpha = 0.4f * waveAlpha),
                                    waveColor.copy(alpha = 0.2f * waveAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(waveX, waveY),
                                radius = outerRadius
                            ),
                            center = Offset(waveX, waveY),
                            radius = outerRadius
                        )

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.9f * waveAlpha),
                                    waveColor.copy(alpha = 0.9f * waveAlpha),
                                    waveColor.copy(alpha = 0.6f * waveAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(waveX, waveY),
                                radius = coreRadius
                            ),
                            center = Offset(waveX, waveY),
                            radius = coreRadius
                        )

                        if (!isProgressDecreasing) {
                            for (i in 1..3) {
                                val ringBaseRadius = coreRadius * 0.5f
                                val ringRadius =
                                    ringBaseRadius * i * (1f + energyWaveOffset.value * 0.6f)
                                val ringAlpha = waveAlpha * (1f - i * 0.25f)

                                drawCircle(
                                    color = waveColor.copy(alpha = 0.5f * ringAlpha),
                                    radius = ringRadius,
                                    center = Offset(waveX, waveY),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 2.5f
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showInfoDialog) {
        Dialog(onDismissRequest = { showInfoDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.largeCornerRadius))
                    .background(color = colors.dialogInfoBackground)
                    .border(
                        width = 1.dp,
                        color = colors.cardBorder,
                        shape = RoundedCornerShape(dimensions.largeCornerRadius)
                    )
                    .padding(dimensions.mediumPadding)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(dimensions.iconSizeLarge)
                        )
                        Spacer(modifier = Modifier.width(dimensions.smallSpacing))
                        Text(
                            text = stringResource(R.string.mastery_info_title),
                            style = typography.cardTitleLarge,
                            color = colors.textMain
                        )
                    }

                    HorizontalDivider(
                        color = colors.textSecondary.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)) {
                        Text(
                            text = stringResource(R.string.mastery_info_general),
                            style = typography.cardDescriptionMedium,
                            color = colors.textMain.copy(alpha = 0.9f)
                        )

                    }

                    Spacer(modifier = Modifier.height(dimensions.smallSpacing))

                    Button(
                        onClick = { showInfoDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accent.copy(alpha = 0.1f),
                            contentColor = colors.accent
                        ),
                        shape = RoundedCornerShape(dimensions.largeCornerRadius),
                        elevation = null
                    ) {
                        Text(
                            text = stringResource(R.string.got_it),
                            style = typography.buttonTextSize.let { typography.cardTitleMedium },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}