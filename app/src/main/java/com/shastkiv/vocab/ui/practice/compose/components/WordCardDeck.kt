package com.shastkiv.vocab.ui.practice.compose.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.shastkiv.vocab.domain.model.Word

private const val VISIBLE_DECK_CARDS = 2
private const val CARD_SCALE_DECREMENT = 0.05f
private const val CARD_OFFSET_DP = 15
private const val CARD_ALPHA_DECREMENT = 0.3f
private const val ANIMATION_DURATION_MS = 300

@Composable
fun WordCardDeck(
    currentWord: Word,
    nextWordsInDeck: List<Word>,
    onListenClick: () -> Unit
) {
    val visibleWords = (listOf(currentWord) + nextWordsInDeck).take(VISIBLE_DECK_CARDS + 1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        visibleWords.forEachIndexed { index, word ->
            // Стабільний та унікальний ключ є КРИТИЧНО важливим для роботи анімації.
            // Якщо у вашої моделі Word є унікальний ID, ОБОВ'ЯЗКОВО використовуйте його: key(word.id).
            key(word.sourceWord) { // <-- ЗАМІНІТЬ ЦЕ НА УНІКАЛЬНИЙ ID, ЯКЩО ВІН Є!

                val isMainCard = index == 0

                val scaleTarget = 1f - (index * CARD_SCALE_DECREMENT)
                val offsetTarget = (index * CARD_OFFSET_DP).dp
                val alphaTarget = if (isMainCard) 1f else 1f - (index * CARD_ALPHA_DECREMENT)

                val animatedScale by animateFloatAsState(
                    targetValue = scaleTarget,
                    animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
                    label = "scale"
                )
                val animatedOffset by animateDpAsState(
                    targetValue = offsetTarget,
                    animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
                    label = "offset"
                )
                val animatedAlpha by animateFloatAsState(
                    targetValue = alphaTarget,
                    animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
                    label = "alpha"
                )

                WordCardView(
                    word = word,
                    // Обробник кліку передається тільки головній картці.
                    // Для задніх карток буде використано пустий обробник за замовчуванням.
                    onListenClick = if (isMainCard) onListenClick else { -> },
                    modifier = Modifier
                        .scale(animatedScale)
                        .align(Alignment.TopCenter)
                        .padding(top = animatedOffset)
                        .alpha(animatedAlpha)
                        .zIndex(-(index.toFloat()))
                )
            }
        }
    }
}