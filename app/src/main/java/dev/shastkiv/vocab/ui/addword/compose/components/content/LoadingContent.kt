package dev.shastkiv.vocab.ui.addword.compose.components.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.addword.compose.components.common.WordInputField
import dev.shastkiv.vocab.ui.theme.appDimensions

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
    val dimensions = MaterialTheme.appDimensions
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.loadingAnimationHeight)
            .padding(top = dimensions.mediumPadding)
    )
}