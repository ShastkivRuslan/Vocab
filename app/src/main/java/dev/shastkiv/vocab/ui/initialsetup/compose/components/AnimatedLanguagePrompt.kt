package dev.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.shastkiv.vocab.ui.theme.dimensions
import kotlinx.coroutines.delay

@Composable
fun AnimatedLanguagePrompt() {
    var currentPhraseIndex by remember { mutableIntStateOf(0) }

    val dimensions = MaterialTheme.dimensions
    val defaultColors = MaterialTheme.colorScheme

    val phrases = remember {
        listOf(
            "Оберіть мову додатку",
            "Choose app language",
            "Wählen Sie die App-Sprache",
            "Choisissez la langue de l'app",
            "Wybierz język aplikacji",
            "Vyberte jazyk aplikace"
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            currentPhraseIndex = (currentPhraseIndex + 1) % phrases.size
        }
    }

    AnimatedContent(
        targetState = phrases[currentPhraseIndex],
        transitionSpec = {
            slideInVertically { height -> height } + fadeIn() togetherWith
                    slideOutVertically { height -> -height } + fadeOut()
        },
        label = "language_prompt",
        modifier = Modifier.fillMaxWidth()
    ) { phrase ->
        Text(
            text = phrase,
            style = dimensions.promptTextStyle,
            textAlign = TextAlign.Center,
            color = defaultColors.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumPadding)
        )
    }
}