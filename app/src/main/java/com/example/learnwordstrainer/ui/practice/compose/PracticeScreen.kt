package com.example.learnwordstrainer.ui.practice.compose

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.domain.model.ExampleData
import com.example.learnwordstrainer.domain.model.Word
import kotlinx.coroutines.delay

// This would be your screen's entry point, similar to PracticeActivity
@Composable
fun PracticeScreen(
    // Pass your ViewModel here to drive the state
    // viewModel: PracticeViewModel
) {
    // Dummy state for demonstration. Replace with your ViewModel state.
    val words = remember {
        mutableStateListOf(
            Word(id = 1, sourceWord = "Edfsfphemeral22", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1"),
            Word(id = 1, sourceWord = "Ephemexcvxcral22", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1"),
            Word(id = 1, sourceWord = "xcvxc", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1"),
            Word(id = 1, sourceWord = "Ephemcxvcxveral22", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1"),
            Word(id = 1, sourceWord = "Ephemcvxvcxeral22", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1"),
            Word(id = 1, sourceWord = "Ephemxcvxcveral22", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1"),
            Word(id = 1, sourceWord = "xcvxcvxc", translation = "Ефемерний", sourceLanguageCode = "en", targetLanguageCode = "uk", wordLevel = "C1")

        )
    }
    var isLoadingExamples by remember { mutableStateOf(true) }
    var examples by remember { mutableStateOf<List<ExampleData>>(emptyList()) }

    // Simulate loading AI examples after a new word appears
    LaunchedEffect(words.firstOrNull()) {
        isLoadingExamples = true
        examples = emptyList()
        delay(1500) // Simulate network delay
        examples = listOf(
            ExampleData("An apple a day keeps the doctor away.", "Яблуко в день тримає лікаря подалі."),
            ExampleData("This apple pie is delicious.", "Цей яблучний пиріг дуже смачний."),
            ExampleData("She took a bite of the crisp, red apple.", "Вона відкусила шматочок хрусткого червоного яблука.")
        )
        isLoadingExamples = false
    }

    Scaffold(
        bottomBar = {
            PracticeFooter(
                onNextWordClick = {
                    if (words.size > 1) {
                        words.removeAt(0)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // The WordCardDeck animates automatically when the 'words' list changes.
            // This declaratively achieves the card swipe animation.
            words.firstOrNull()?.let { currentWord ->
                WordCardDeck(
                    currentWord = currentWord,
                    nextWordsInDeck = words.drop(1),
                    onListenClick = { /* TODO: Handle TTS click */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The AiChatView now contains the typewriter animation logic.
            AiChatView(
                examples = examples,
                isLoading = isLoadingExamples
            )
        }
    }
}


// --- REPLICATED ANIMATIONS AND COMPONENTS ---

//region Word Card Deck
private const val VISIBLE_DECK_CARDS = 2
private const val CARD_SCALE_DECREMENT = 0.05f
private const val CARD_OFFSET_DP = 15
private const val CARD_ALPHA_DECREMENT = 0.3f
private const val ANIMATION_DURATION_MS = 300
// Нова константа для зміщення головної картки вгору
private const val MAIN_CARD_SWIPE_UP_DISTANCE_DP = -100 // Від'ємне значення для руху вгору

@Composable
fun WordCardDeck(
    currentWord: Word,
    nextWordsInDeck: List<Word>,
    onListenClick: () -> Unit
) {
    val visibleWords = (listOf(currentWord) + nextWordsInDeck).take(VISIBLE_DECK_CARDS + 1)

    // Використовуємо `key` для того, щоб Compose правильно ідентифікував картки
    // і застосовував до них анімації при зміні порядку.
    // Якщо у Word є унікальний ID, краще використовувати його: key(word.id)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        visibleWords.asReversed().forEachIndexed { indexFromBack, word ->
            val index = visibleWords.size - 1 - indexFromBack
            key(word.sourceWord) { // Використовуємо sourceWord як key, оскільки ID немає
                val isMainCard = index == 0

                val scaleTarget = 1f - (index * CARD_SCALE_DECREMENT)
                val offsetTarget = (index * CARD_OFFSET_DP).dp
                val alphaTarget = 1f - (index * CARD_ALPHA_DECREMENT)

                // Анімація для зміщення вгору та прозорості головної картки
                val animatedMainCardOffset by animateDpAsState(
                    targetValue = if (isMainCard && nextWordsInDeck.isNotEmpty()) MAIN_CARD_SWIPE_UP_DISTANCE_DP.dp else 0.dp,
                    animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
                    label = "mainCardOffset"
                )
                val animatedMainCardAlpha by animateFloatAsState(
                    targetValue = if (isMainCard && nextWordsInDeck.isNotEmpty()) 0f else 1f, // Зникає, коли є наступні слова
                    animationSpec = tween(durationMillis = ANIMATION_DURATION_MS),
                    label = "mainCardAlpha"
                )

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
                    onListenClick = if (isMainCard) onListenClick else { -> },
                    modifier = Modifier
                        .scale(animatedScale)
                        // Застосовуємо зміщення для головної картки тільки при її зникненні
                        .offset(y = if (isMainCard) animatedMainCardOffset else animatedOffset)
                        // Застосовуємо анімовану прозорість для головної картки
                        .alpha(if (isMainCard) animatedMainCardAlpha else animatedAlpha)
                        // Збільшуємо zIndex для головної картки, щоб вона була зверху
                        .zIndex(if (isMainCard) 1f else 0f)
                )
            }
        }
    }
}


@Composable
fun WordCardView(
    word: Word,
    modifier: Modifier = Modifier,
    onListenClick: () -> Unit = {}
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.practice_mode),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onListenClick) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = word.sourceWord,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = word.translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
//endregion

//region AI Chat View
/**
 * This is the updated AiChatView. It now internally handles the sequential
 * typewriter animation for the chat messages, identical to the XML version's logic.
 */
@Composable
fun AiChatView(
    examples: List<ExampleData>,
    isLoading: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Vocab AI",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ai_loading))
                    LottieAnimation(composition, modifier = Modifier.size(80.dp))
                }
            } else {
                // We use a simple Column because the number of examples is small and fixed.
                // A LaunchedEffect orchestrates the sequential animation.
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // State to hold the currently visible text for each part of each message.
                    val animatedMessages = remember { mutableStateListOf<Pair<String, String>>() }

                    // This effect runs when 'examples' changes. It animates each message one by one.
                    LaunchedEffect(examples) {
                        animatedMessages.clear()
                        examples.forEach { exampleData ->
                            val currentExampleText = StringBuilder()
                            val currentTranslationText = StringBuilder()
                            animatedMessages.add("" to "")
                            val currentIndex = animatedMessages.lastIndex

                            // Animate example text
                            exampleData.exampleText.forEach { char ->
                                currentExampleText.append(char)
                                animatedMessages[currentIndex] = currentExampleText.toString() to currentTranslationText.toString()
                                delay(15) // Delay between characters
                            }

                            // Animate translation text
                            exampleData.translationText.forEach { char ->
                                currentTranslationText.append(char)
                                animatedMessages[currentIndex] = currentExampleText.toString() to currentTranslationText.toString()
                                delay(15)
                            }
                        }
                    }

                    // Display the animated messages
                    animatedMessages.forEach { (example, translation) ->
                        ChatMessageBubble(
                            exampleText = example,
                            translationText = translation
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(exampleText: String, translationText: String) {
    // A simple check to not show the bubble until the text starts appearing
    if (exampleText.isNotBlank()) {
        Column {
            Text(text = exampleText, style = MaterialTheme.typography.bodyMedium)
            if (translationText.isNotBlank()) {
                Text(
                    text = translationText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
//endregion

//region Footer
@Composable
fun PracticeFooter(
    onNextWordClick: () -> Unit
) {
    // Using a simple Box with padding for placement at the bottom
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = onNextWordClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.next_word))
        }
    }
}
//endregion