package dev.shastkiv.vocab.ui.flashcard.compose.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.ui.components.MasteryBadge
import dev.shastkiv.vocab.ui.theme.GreenSuccess
import dev.shastkiv.vocab.ui.theme.RedError
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Deck constants — ті самі що в PracticeScreen
private const val VISIBLE_DECK_CARDS = 2
private const val CARD_SCALE_DECREMENT = 0.05f
private const val CARD_OFFSET_DP = 14
private const val CARD_ALPHA_DECREMENT = 0.25f
private const val DECK_ANIMATION_MS = 300

// Swipe / flip / exit
private const val SWIPE_THRESHOLD = 120f
private const val FLIP_DURATION_MS = 440
private const val EXIT_DURATION_MS = 380

/**
 * Колода флешкарток — головна картка по центру екрану,
 * під нею видно 2 картки стеку (менші + зміщені вниз).
 *
 * Після відповіді:
 *  1. Картка фліпується (показує переклад)
 *  2. Через короткий delay — виходить вниз
 *  3. Наступна картка (яка вже була видна в стеку) плавно піднімається на місце
 *
 * @param currentWord      поточне слово (верхня картка)
 * @param nextWords        наступні слова — перші 2 показуються в стеку
 * @param isAnswered       чи вже дана відповідь
 * @param isAnswerCorrect  результат відповіді (null — ще не відповіли)
 * @param onKnow           колбек "Знаю"
 * @param onDontKnow       колбек "Не знаю"
 * @param onListenClick    колбек TTS
 * @param onExitComplete   викликається коли картка повністю вийшла з екрану
 */
@Composable
fun FlashCardDeck(
    currentWord: Word,
    nextWords: List<Word>,
    isAnswered: Boolean,
    isAnswerCorrect: Boolean?,
    onKnow: () -> Unit,
    onDontKnow: () -> Unit,
    onListenClick: () -> Unit,
    onExitComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleWords = remember(currentWord, nextWords) {
        (listOf(currentWord) + nextWords).take(VISIBLE_DECK_CARDS + 1)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Рендеримо з кінця — нижні картки першими, щоб верхня була зверху
        visibleWords.asReversed().forEachIndexed { indexFromBack, word ->
            val index = visibleWords.size - 1 - indexFromBack
            val isMain = index == 0

            key(word.id) {
                val animatedScale by animateFloatAsState(
                    targetValue = 1f - index * CARD_SCALE_DECREMENT,
                    animationSpec = tween(DECK_ANIMATION_MS),
                    label = "scale_$index"
                )
                val animatedOffsetY by animateDpAsState(
                    targetValue = (index * CARD_OFFSET_DP).dp,
                    animationSpec = tween(DECK_ANIMATION_MS),
                    label = "offsetY_$index"
                )
                val animatedAlpha by animateFloatAsState(
                    targetValue = 1f - index * CARD_ALPHA_DECREMENT,
                    animationSpec = tween(DECK_ANIMATION_MS),
                    label = "alpha_$index"
                )

                if (isMain) {
                    MainFlashCard(
                        word = word,
                        isAnswered = isAnswered,
                        isAnswerCorrect = isAnswerCorrect,
                        onKnow = onKnow,
                        onDontKnow = onDontKnow,
                        onListenClick = onListenClick,
                        onExitComplete = onExitComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .zIndex(10f)
                    )
                } else {
                    // Картки стеку — статичні
                    DeckCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .scale(animatedScale)
                            .offset(y = animatedOffsetY)
                            .alpha(animatedAlpha)
                            .zIndex(index.toFloat())
                    )
                }
            }
        }
    }
}

// ─── Картка стеку (задня) ─────────────────────────────────────────────────────

@Composable
private fun DeckCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.appColors.cardBackground)
            .border(1.dp, MaterialTheme.appColors.cardBorder, RoundedCornerShape(24.dp))
    )
}

// ─── Головна картка ────────────────────────────────────────────────────────────

@Composable
private fun MainFlashCard(
    word: Word,
    isAnswered: Boolean,
    isAnswerCorrect: Boolean?,
    onKnow: () -> Unit,
    onDontKnow: () -> Unit,
    onListenClick: () -> Unit,
    onExitComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var dragX by remember { mutableFloatStateOf(0f) }
    val animDragX = remember { Animatable(0f) }
    val flipAnim = remember { Animatable(0f) }
    val exitY = remember { Animatable(0f) }
    var hasFlipped by remember { mutableStateOf(false) }

    // Скидаємо стан при новому слові
    LaunchedEffect(word.id) {
        hasFlipped = false
        dragX = 0f
        animDragX.snapTo(0f)
        flipAnim.snapTo(0f)
        exitY.snapTo(0f)
    }

    // Фліп → пауза → вихід вниз
    LaunchedEffect(isAnswered) {
        if (isAnswered && !hasFlipped) {
            hasFlipped = true
            animDragX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
            dragX = 0f
            // Фліп
            flipAnim.animateTo(
                targetValue = 180f,
                animationSpec = tween(FLIP_DURATION_MS)
            )
            // Пауза — показуємо переклад
            kotlinx.coroutines.delay(600)
            // Вихід вниз
            exitY.animateTo(
                targetValue = 1400f,
                animationSpec = tween(EXIT_DURATION_MS, easing = FastOutSlowInEasing)
            )
            onExitComplete()
        }
    }

    val swipeDir = when {
        animDragX.value > 40f -> 1
        animDragX.value < -40f -> -1
        else -> 0
    }

    Box(
        modifier = modifier
            .offset { IntOffset(animDragX.value.roundToInt(), exitY.value.roundToInt()) }
            .pointerInput(isAnswered) {
                if (!isAnswered) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                when {
                                    dragX > SWIPE_THRESHOLD -> {
                                        onKnow()
                                        animDragX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                                    }
                                    dragX < -SWIPE_THRESHOLD -> {
                                        onDontKnow()
                                        animDragX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                                    }
                                    else -> animDragX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy))
                                }
                                dragX = 0f
                            }
                        },
                        onDragCancel = {
                            scope.launch { animDragX.animateTo(0f); dragX = 0f }
                        },
                        onHorizontalDrag = { _, delta ->
                            dragX += delta
                            scope.launch { animDragX.snapTo(dragX) }
                        }
                    )
                }
            }
    ) {
        if (flipAnim.value <= 90f) {
            CardFront(
                word = word,
                swipeDir = swipeDir,
                dragX = animDragX.value,
                tilt = animDragX.value / 20f,
                flipRot = flipAnim.value,
                onListenClick = onListenClick,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CardBack(
                word = word,
                isCorrect = isAnswerCorrect,
                flipRot = flipAnim.value,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ─── Передня сторона ───────────────────────────────────────────────────────────

@Composable
private fun CardFront(
    word: Word,
    swipeDir: Int,
    dragX: Float,
    tilt: Float,
    flipRot: Float,
    onListenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val aiData = remember(word.id) { word.getAIData() }

    val overlayAlpha = when (swipeDir) {
        1 -> (dragX / SWIPE_THRESHOLD).coerceIn(0f, 0.35f)
        -1 -> (-dragX / SWIPE_THRESHOLD).coerceIn(0f, 0.35f)
        else -> 0f
    }
    val overlayColor = when (swipeDir) {
        1 -> GreenSuccess.copy(alpha = overlayAlpha)
        -1 -> RedError.copy(alpha = overlayAlpha)
        else -> Color.Transparent
    }
    val borderColor = when (swipeDir) {
        1 -> GreenSuccess.copy(alpha = (overlayAlpha * 2f).coerceIn(0f, 0.6f))
        -1 -> RedError.copy(alpha = (overlayAlpha * 2f).coerceIn(0f, 0.6f))
        else -> colors.cardBorder
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = tilt
                rotationY = flipRot
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(24.dp))
            .background(colors.cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
    ) {
        // Swipe overlay
        if (overlayColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
            )
        }

        // Swipe label
        SwipeLabel(
            swipeDir = swipeDir,
            modifier = Modifier
                .align(if (swipeDir == 1) Alignment.TopEnd else Alignment.TopStart)
                .padding(top = 14.dp, start = 14.dp, end = 14.dp)
        )

        // Слово + badge + транскрипція — центровано
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = word.sourceWord,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1.5).sp,
                    textAlign = TextAlign.Center,
                    color = colors.cardTitleText,
                    lineHeight = 38.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                MasteryBadge(
                    score = word.masteryScore
                )
            }

            aiData?.transcription?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "[$it]",
                    style = typography.cardDescriptionMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Підказка свайпу + TTS внизу
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 14.dp, start = 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "← не знаю",
                style = typography.cardDescriptionSmall,
                color = colors.textSecondary.copy(alpha = 0.35f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("·", color = colors.textSecondary.copy(alpha = 0.25f), fontSize = 10.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "знаю →",
                style = typography.cardDescriptionSmall,
                color = colors.textSecondary.copy(alpha = 0.35f)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onListenClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = colors.textSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ─── Задня сторона ─────────────────────────────────────────────────────────────

@Composable
private fun CardBack(
    word: Word,
    isCorrect: Boolean?,
    flipRot: Float,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.appColors

    val bgColor = when (isCorrect) {
        true -> GreenSuccess.copy(alpha = 0.07f)
        false -> RedError.copy(alpha = 0.07f)
        null -> colors.cardBackground
    }
    val borderColor = when (isCorrect) {
        true -> GreenSuccess.copy(alpha = 0.28f)
        false -> RedError.copy(alpha = 0.28f)
        null -> colors.cardBorder
    }
    val labelText = when (isCorrect) {
        true -> "знав ✓"
        false -> "не знав ✕"
        null -> ""
    }
    val labelColor = when (isCorrect) {
        true -> GreenSuccess
        false -> RedError
        null -> colors.textSecondary
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = flipRot - 180f
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            if (labelText.isNotEmpty()) {
                Text(
                    text = labelText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = labelColor.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = word.translation,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center,
                color = colors.cardTitleText,
                lineHeight = 34.sp
            )
        }
    }
}

// ─── Swipe label ───────────────────────────────────────────────────────────────

@Composable
private fun SwipeLabel(swipeDir: Int, modifier: Modifier = Modifier) {
    if (swipeDir == 0) return
    val text = if (swipeDir == 1) "Знаю" else "Не знаю"
    val color = if (swipeDir == 1) GreenSuccess else RedError

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            color = color
        )
    }
}