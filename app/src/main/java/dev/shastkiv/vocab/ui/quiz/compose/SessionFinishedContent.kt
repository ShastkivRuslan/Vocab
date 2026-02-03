package dev.shastkiv.vocab.ui.quiz.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.quiz.state.RepetitionUiState
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun SessionFinishedContent(
    state: RepetitionUiState.SessionFinished,
    onContinueClick: () -> Unit,
    onFinishClick: () -> Unit,
    onMistakesReviewClick: () -> Unit
) {
    val colors = MaterialTheme.appColors
    val dims = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val slideOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "slide"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dims.largeCornerRadius))
                .background(
                    color = colors.cardBackground,
                    shape = RoundedCornerShape(dims.largeCornerRadius)
                )
                .border(
                    width = dims.masteryProgressBorderWidth,
                    color = colors.cardBorder,
                    shape = RoundedCornerShape(dims.largeCornerRadius)
                )
                .graphicsLayer {
                    translationY = slideOffset
                    this.alpha = alpha
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dims.largePadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dims.mediumSpacing)
            ) {
                Text(
                    text = stringResource(R.string.session_finished),
                    style = typography.header.copy(
                        fontWeight = FontWeight.Light,
                        letterSpacing = typography.header.letterSpacing
                    ),
                    color = colors.textMain.copy(alpha = 0.9f)
                )

                CircularStats(
                    correct = state.totalCorrect,
                    wrong = state.totalWrong,
                    accentColor = colors.accent,
                    dims = dims,
                    typography = typography,
                    colors = colors
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MinimalStatItem(
                        value = state.totalCorrect,
                        label = stringResource(R.string.correct),
                        color = colors.greenSuccess,
                        delay = 300,
                        dims = dims,
                        typography = typography,
                        colors = colors
                    )

                    Box(
                        modifier = Modifier
                            .width(dims.masteryProgressBorderWidth)
                            .height(60.dp)
                            .background(colors.cardBorder.copy(alpha = 0.3f))
                    )

                    MinimalStatItem(
                        value = state.totalWrong,
                        label = stringResource(R.string.mistakes),
                        color = colors.redError,
                        delay = 400,
                        dims = dims,
                        typography = typography,
                        colors = colors
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dims.mediumPadding),
            verticalArrangement = Arrangement.spacedBy(dims.mediumSpacing)
        ) {
            if (state.totalWrong > 0) {
                Button(
                    onClick = onMistakesReviewClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dims.buttonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.redError.copy(alpha = 0.15f),
                        contentColor = colors.redError
                    ),
                    shape = RoundedCornerShape(dims.mediumCornerRadius),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        colors.redError.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.review_mistakes),
                        style = typography.buttonTextSize.let { typography.cardTitleMedium },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dims.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent.copy(alpha = 0.1f),
                    contentColor = colors.accent
                ),
                shape = RoundedCornerShape(dims.mediumCornerRadius)
            ) {
                Text(
                    text = stringResource(R.string.choose_new_mode),
                    style = typography.buttonTextSize.let { typography.cardTitleMedium },
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onFinishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dims.buttonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = colors.onAccent
                ),
                shape = RoundedCornerShape(dims.mediumCornerRadius),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.finish_for_today),
                    style = typography.buttonTextSize.let { typography.cardTitleMedium },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CircularStats(
    correct: Int,
    wrong: Int,
    accentColor: Color,
    dims: dev.shastkiv.vocab.ui.theme.AppDimensions,
    typography: dev.shastkiv.vocab.ui.theme.CustomTypography,
    colors: dev.shastkiv.vocab.ui.theme.CustomAppColors
) {
    val total = correct + wrong
    val correctPercent = if (total > 0) correct.toFloat() / total else 0f

    var animationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        animationPlayed = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) correctPercent else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(dims.statisticsCircleSize + dims.largePadding)
    ) {
        // Світіння
        Canvas(
            modifier = Modifier
                .size(dims.statisticsCircleSize + dims.largePadding + dims.mediumSpacing)
                .blur(dims.statisticBoxBlurSize)
        ) {
            drawCircle(
                color = colors.progressGlowColor.copy(alpha = glowAlpha),
                radius = size.minDimension / 2
            )
        }

        // Основне коло
        Canvas(modifier = Modifier.size(dims.statisticsCircleSize)) {
            val strokeWidth = dims.progressCircleStrokeWidth.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Фонове коло
            drawCircle(
                color = colors.progressBackgroundCircleColor,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Прогрес
            drawArc(
                color = accentColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                ),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                )
            )
        }

        // Центральний текст
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(correctPercent * 100).toInt()}%",
                style = typography.progressPercent.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = typography.progressPercent.letterSpacing
                ),
                color = colors.progressCenterTextTitle
            )
            Text(
                text = stringResource(R.string.accuracy),
                style = typography.progressLabel.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = typography.progressLabel.letterSpacing
                ),
                color = colors.progressCenterTextSubtitle
            )
        }
    }
}

@Composable
private fun MinimalStatItem(
    value: Int,
    label: String,
    color: Color,
    delay: Int,
    dims: dev.shastkiv.vocab.ui.theme.AppDimensions,
    typography: dev.shastkiv.vocab.ui.theme.CustomTypography,
    colors: dev.shastkiv.vocab.ui.theme.CustomAppColors
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    val animatedValue by animateIntAsState(
        targetValue = if (visible) value else 0,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "value"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(alpha)
    ) {
        Text(
            text = animatedValue.toString(),
            style = typography.progressPercent.copy(
                fontWeight = FontWeight.Light,
                letterSpacing = typography.progressPercent.letterSpacing
            ),
            color = color
        )
        Spacer(modifier = Modifier.height(dims.extraSmallSpacing))
        Text(
            text = label.uppercase(),
            style = typography.progressLabel.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = typography.progressLabel.letterSpacing
            ),
            color = colors.statsLabelText
        )
    }
}