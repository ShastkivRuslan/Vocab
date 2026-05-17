package dev.shastkiv.vocab.ui.addword.overlay.compose

import android.graphics.BlurMaskFilter
import android.graphics.PathMeasure
import android.graphics.RectF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DialogContainer(
    isLoading: Boolean = false,
    pulseKey: Any = Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val neonColor = colors.neonAccent

    // Intro pulse — один спалах при відкритті → static
    val glowAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        glowAlpha.animateTo(1f, tween(2000, easing = FastOutSlowInEasing))
        glowAlpha.animateTo(GLOW_STATIC_ALPHA, tween(1500, easing = FastOutSlowInEasing))
    }

    // Pulse при зміні стану діалогу
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(pulseKey) {
        if (!isInitialized) {
            isInitialized = true
            return@LaunchedEffect
        }
        glowAlpha.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
        glowAlpha.animateTo(GLOW_STATIC_ALPHA, tween(600, easing = FastOutSlowInEasing))
    }

    // Breathing spots під час завантаження
    val spotAlphas = remember { List(SPOT_COUNT) { Animatable(0f) } }
    val spotPositions = remember { List(SPOT_COUNT) { i -> Animatable(SPOT_FRACTIONS[i]) } }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            spotAlphas.forEachIndexed { i, anim ->
                // Дихання яскравості
                launch {
                    delay(i * SPOT_STAGGER_MS)
                    while (true) {
                        val peak = 0.6f + Random.nextFloat() * 0.4f
                        val trough = 0.04f + Random.nextFloat() * 0.12f
                        val upMs = 600 + Random.nextInt(800)
                        val downMs = 700 + Random.nextInt(900)
                        anim.animateTo(peak, tween(upMs, easing = FastOutSlowInEasing))
                        anim.animateTo(trough, tween(downMs, easing = FastOutSlowInEasing))
                    }
                }
                // Хаотичне блукання по периметру
                launch {
                    delay(i * SPOT_STAGGER_MS + Random.nextLong(400))
                    while (true) {
                        val current = spotPositions[i].value
                        val delta = (Random.nextFloat() - 0.5f) * 0.28f
                        val next = (current + delta).coerceIn(0.02f, 0.98f)
                        val travelMs = 1200 + Random.nextInt(2000)
                        spotPositions[i].animateTo(next, tween(travelMs, easing = FastOutSlowInEasing))
                    }
                }
            }
        } else {
            spotAlphas.forEach { launch { it.animateTo(0f, tween(600, easing = FastOutSlowInEasing)) } }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismissRequest
            )
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                .wrapContentSize()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = dimensions.mediumPadding)
                    .drawBehind {
                        val cornerPx = dimensions.largeCornerRadius.toPx()
                        val paint = Paint()
                        val frameworkPaint = paint.asFrameworkPaint()
                        frameworkPaint.color = android.graphics.Color.TRANSPARENT
                        frameworkPaint.setShadowLayer(
                            55f, 0f, 0f,
                            android.graphics.Color.argb(
                                (180 * glowAlpha.value).toInt(),
                                (neonColor.red * 255).toInt(),
                                (neonColor.green * 255).toInt(),
                                (neonColor.blue * 255).toInt()
                            )
                        )
                        drawIntoCanvas { canvas ->
                            canvas.drawRoundRect(
                                0f, 0f, size.width, size.height,
                                cornerPx, cornerPx, paint
                            )
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(dimensions.largeCornerRadius))
                        .background(colors.overlayDialogColor)
                        .drawWithContent {
                            drawContent()

                            val cornerPx = dimensions.largeCornerRadius.toPx()
                            val borderPx = 1.5.dp.toPx()

                            // Статичний neon border
                            val staticPath = Path().apply {
                                addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(cornerPx)))
                            }
                            drawPath(
                                staticPath,
                                color = neonColor.copy(alpha = (0.45f * glowAlpha.value).coerceIn(0f, 1f)),
                                style = Stroke(borderPx)
                            )

                            // Breathing spots під час завантаження
                            val hasActiveSpots = spotAlphas.any { it.value > 0.01f }
                            if (hasActiveSpots) {
                                val androidPath = android.graphics.Path().apply {
                                    addRoundRect(
                                        RectF(0f, 0f, size.width, size.height),
                                        cornerPx, cornerPx,
                                        android.graphics.Path.Direction.CW
                                    )
                                }
                                val measure = PathMeasure(androidPath, false)
                                val totalLength = measure.length
                                val halfSpan = totalLength * SPOT_HALF_SPAN_FRACTION

                                spotAlphas.forEachIndexed { i, alphaAnim ->
                                    val alpha = alphaAnim.value
                                    if (alpha < 0.01f) return@forEachIndexed

                                    val center = spotPositions[i].value * totalLength
                                    val seg = android.graphics.Path()
                                    measure.getSegment(
                                        (center - halfSpan).coerceAtLeast(0f),
                                        (center + halfSpan).coerceAtMost(totalLength),
                                        seg, true
                                    )

                                    drawIntoCanvas { canvas ->
                                        val native = canvas.nativeCanvas

                                        // Дуже широкий розмитий ореол
                                        native.drawPath(seg, android.graphics.Paint().apply {
                                            isAntiAlias = true
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = borderPx * 2f
                                            strokeCap = android.graphics.Paint.Cap.ROUND
                                            color = neonColor.copy(alpha = alpha * 0.22f).toArgb()
                                            maskFilter = BlurMaskFilter(borderPx * 18f, BlurMaskFilter.Blur.NORMAL)
                                        })

                                        // Середній шар
                                        native.drawPath(seg, android.graphics.Paint().apply {
                                            isAntiAlias = true
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = borderPx * 2f
                                            strokeCap = android.graphics.Paint.Cap.ROUND
                                            color = neonColor.copy(alpha = alpha * 0.5f).toArgb()
                                            maskFilter = BlurMaskFilter(borderPx * 7f, BlurMaskFilter.Blur.NORMAL)
                                        })

                                        // М'який яскравий core
                                        native.drawPath(seg, android.graphics.Paint().apply {
                                            isAntiAlias = true
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeWidth = borderPx
                                            strokeCap = android.graphics.Paint.Cap.ROUND
                                            color = neonColor.copy(alpha = alpha * 0.9f).toArgb()
                                            maskFilter = BlurMaskFilter(borderPx * 2f, BlurMaskFilter.Blur.NORMAL)
                                        })
                                    }
                                }
                            }
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = false
                        ) {}
                ) {
                    Column {
                        content()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensions.dragBoxHeight)
                                .pointerInput(Unit) {
                                    detectDragGestures { _, dragAmount ->
                                        offset += dragAmount
                                    }
                                }
                                .clickable(enabled = false) {},
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(dimensions.dragLineWidth)
                                    .height(dimensions.dragLineHeight)
                                    .background(
                                        color = colors.cardTitleText.copy(alpha = 0.80f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val GLOW_STATIC_ALPHA = 0.45f
private const val SPOT_COUNT = 5
private const val SPOT_STAGGER_MS = 200L
private const val SPOT_HALF_SPAN_FRACTION = 0.07f
private val SPOT_FRACTIONS = listOf(0.08f, 0.26f, 0.47f, 0.66f, 0.86f)
