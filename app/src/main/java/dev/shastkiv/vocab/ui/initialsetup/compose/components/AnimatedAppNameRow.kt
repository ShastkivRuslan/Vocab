package dev.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.delay

@Composable
fun AnimatedAppNameRow() {
    var isVisible by remember { mutableStateOf(false) }

    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    Row(
        modifier = Modifier.height(dimensions.appNameHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val appName = stringResource(R.string.app_name_without_dot)

        appName.forEachIndexed { index, char ->
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = index * 60,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 700,
                        delayMillis = index * 60,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleIn(
                    initialScale = 0.5f,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = index * 60,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Text(
                    text = char.toString(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = colors.textMain,
                    style = typography.animatedAppName
                )
            }
        }

        Spacer(modifier = Modifier.width(dimensions.smallSpacing))

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = appName.length * 80 + 200,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    delayMillis = appName.length * 80 + 200,
                    easing = FastOutSlowInEasing
                )
            ) + scaleIn(
                initialScale = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                    visibilityThreshold = 0.01f
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.headerIconSize * 0.6f)
                    .background(
                        color = colors.cardBackground,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = colors.cardBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Bubble",
                    tint = colors.accent,
                    modifier = Modifier.size(dimensions.largeSpacing)
                )
            }
        }
    }
}
