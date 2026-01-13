package dev.shastkiv.vocab.ui.addword.compose.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.UiError
import dev.shastkiv.vocab.ui.addword.compose.components.common.PrimaryButton
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun ErrorContent(
    error: UiError,
    onRetry: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(dimensions.mediumPadding)
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(error.animationRes))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.loadingAnimationHeight)
        )

        Spacer(modifier = Modifier.height(dimensions.largeSpacing))

        Text(
            text = stringResource(id = error.title),
            style = typography.wordHeadLine,
            fontWeight = FontWeight.Bold,
            color = colors.onAccent,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensions.smallSpacing))

        Text(
            text = stringResource(id = error.description),
            style = typography.cardTitleMedium,
            color = colors.cardTitleText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensions.largeSpacing))

        PrimaryButton(
            text = stringResource(R.string.try_again),
            onClick = onRetry
        )
    }
}