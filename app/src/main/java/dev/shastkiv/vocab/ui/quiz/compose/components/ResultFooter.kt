package dev.shastkiv.vocab.ui.quiz.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.GreenSuccess
import dev.shastkiv.vocab.ui.theme.RedError
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.delay

@Composable
fun ResultFooter(
    isCorrect: Boolean,
    onNextWordClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    val stateColor = if (isCorrect) GreenSuccess else RedError
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val totalTime = 3000L
        val stepTime = 16L
        val steps = totalTime / stepTime

        for (i in 0..steps.toInt()) {
            progress = i.toFloat() / steps
            delay(stepTime)
        }
        onNextWordClick()
    }

    val shape = RoundedCornerShape(dimensions.largeCornerRadius)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(stateColor.copy(alpha = 0.35f), Color.Transparent),
                    startY = 0f,
                    endY = 500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(stateColor.copy(alpha = 0.4f), Color.Transparent)
                    ),
                    shape = shape
                )
                .padding(dimensions.mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = dimensions.smallSpacing)
            ) {
                Text(
                    text = if (isCorrect) stringResource(R.string.correct) else stringResource(R.string.wrong),
                    style = typography.wordHeadLine,
                    color = colors.textMain,
                    modifier = Modifier.padding(start = dimensions.smallSpacing)
                )
            }

            Button(
                onClick = onNextWordClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.buttonHeight)
                    .shadow(
                        elevation = 8.dp,
                        spotColor = stateColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(dimensions.largeCornerRadius)
                    ),
                shape = RoundedCornerShape(dimensions.largeCornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = stateColor,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            color = Color.White.copy(alpha = 0.25f),
                            size = size.copy(width = size.width * progress)
                        )
                    }

                    Text(
                        text = stringResource(R.string.next_word),
                        style = typography.cardTitleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}