package com.example.learnwordstrainer.ui.addwordfloating.compose.components.content

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.ui.addwordfloating.compose.components.common.WordInputField

@Composable
fun LoadingContent(word: String) {
    WordInputField(
        value = TextFieldValue(word),
        onValueChange = {},
        onClear = {},
        readOnly = true
    )

    LoadingAnimation()
}

@Composable
private fun LoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_loading))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxWidth()
            .height(136.dp)
            .padding(top = 16.dp)
    )
}