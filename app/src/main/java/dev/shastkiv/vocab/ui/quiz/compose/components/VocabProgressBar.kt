package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

enum class SegmentState {
    IDLE,
    ACTIVE,
    CORRECT,
    WRONG
}
@Composable
fun VocabProgressBar(
    totalSteps: Int,
    currentStep: Int,
    segmentStates: List<SegmentState>,
    modifier: Modifier = Modifier
) {
    val dimensions = MaterialTheme.appDimensions

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
    ) {
        Text(
            text = "Слово ${currentStep + 1} з $totalSteps",
            style = dimensions.progressLabelStyle,
            color = MaterialTheme.appColors.textSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.linearProgressContainerHeight),
            horizontalArrangement = Arrangement.spacedBy(dimensions.extraSmallSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            segmentStates.forEachIndexed { index, state ->
                val isActive = index == currentStep

                val weight by animateFloatAsState(
                    targetValue = if (isActive) 1.5f else 1.0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "segmentWeight"
                )

                ProgressSegment(
                    state = state,
                    isActive = isActive,
                    modifier = Modifier.weight(weight)
                )
            }
        }
    }
}

@Composable
private fun ProgressSegment(
    state: SegmentState,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    val idleColor = colors.vocabProgressIdle
    val correctColor = colors.vocabProgressCorrect
    val wrongColor = colors.vocabProgressWrong
    val activeColor = colors.vocabProgressActive

    val targetColor = when (state) {
        SegmentState.IDLE -> if (isActive) activeColor else idleColor
        SegmentState.CORRECT -> correctColor
        SegmentState.WRONG -> wrongColor
        else -> activeColor
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 300),
        label = "segmentColor"
    )

    val targetHeight = if (isActive) {
        dimensions.linearProgressActiveThickness
    } else {
        dimensions.linearProgressInactiveThickness
    }

    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "segmentHeight"
    )

    Box(
        modifier = modifier
            .height(animatedHeight)
            .background(
                color = animatedColor,
                shape = RoundedCornerShape(animatedHeight / 2)
            )
    )
}