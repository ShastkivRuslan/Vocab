package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.enums.ExerciseType
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.launch

@Composable
fun WordCard(
    word: Word,
    onListenClick: () -> Unit
) {
    val colors = MaterialTheme.appColors
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    val animatedOffsetY = remember { Animatable(- 100f) }
    val animatedAlpha = remember { Animatable(0f) }
    val animatedScale = remember { Animatable(0.8f) }

    LaunchedEffect(word.id) {
        animatedOffsetY.snapTo(- 100f)
        animatedAlpha.snapTo(0f)
        animatedScale.snapTo(0.8f)

        launch { animatedOffsetY.animateTo(0f,tween(200)) }
        launch { animatedAlpha.animateTo(1f, tween(200)) }
        launch { animatedScale.animateTo(1f,tween(200)) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = animatedOffsetY.value
                alpha = animatedAlpha.value
                scaleX = animatedScale.value
                scaleY = animatedScale.value
            }
            .clip(RoundedCornerShape(dimensions.largeCornerRadius))
            .background(
                color = colors.cardBackground,
                shape = RoundedCornerShape(dimensions.largeCornerRadius)
            )
            .border(
                width = 1.dp,
                color = colors.cardBorder,
                shape = RoundedCornerShape(dimensions.largeCornerRadius)
            )
    ) {
        Column(
            modifier = Modifier.padding(dimensions.mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = word.sourceWord,
                    style = typography.header,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = colors.cardTitleText
                )

                IconButton(onClick = onListenClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = colors.textMain)
                }
            }

            Spacer(modifier = Modifier.height(dimensions.largeSpacing))

            MasteryProgressBar(
                masteryScore = word.masteryScore,
                exerciseType = ExerciseType.QUIZ,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

        }
    }
}
