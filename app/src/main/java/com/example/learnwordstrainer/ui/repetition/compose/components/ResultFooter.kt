package com.example.learnwordstrainer.ui.repetition.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.ui.theme.GreenSuccess
import com.example.learnwordstrainer.ui.theme.RedError

@Composable
fun ResultFooter(
    isCorrect: Boolean,
    onNextWordClick: () -> Unit
) {
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(if (isCorrect) R.raw.correct_anim else R.raw.wrong_anim)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) GreenSuccess.copy(alpha = 0.9f) else RedError.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LottieAnimation(
                    composition = lottieComposition,
                    iterations = 1,
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = if (isCorrect) stringResource(R.string.correct) else stringResource(R.string.wrong),
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNextWordClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = if (isCorrect) GreenSuccess else RedError
                )
            ) {
                Text(text = stringResource(R.string.next_word))
            }
        }
    }
}