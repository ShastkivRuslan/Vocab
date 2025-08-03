package com.example.learnwordstrainer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.learnwordstrainer.R

@Composable
fun AddWordDialog(
    onDismissRequest: () -> Unit
) {
    var word by remember { mutableStateOf(TextFieldValue("")) }
    var uiState by remember { mutableStateOf<UiState>(UiState.Idle) }
    var aiResponse by remember { mutableStateOf<WordData?>(null) }
    var isMainSectionExpanded by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismissRequest() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState == UiState.Success && !isMainSectionExpanded) {
                            "${aiResponse?.originalWord} → ${aiResponse?.translation}"
                        } else {
                            stringResource(R.string.add_new_word)
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (uiState == UiState.Success) {
                        IconButton(onClick = { /* TODO: Implement TTS */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_speaker),
                                contentDescription = stringResource(R.string.tts_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close_bubble),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (uiState) {
                    UiState.Idle -> {
                        IdleStateContent(
                            word = word,
                            onWordChange = { word = it },
                            onCheckClick = {
                                uiState = UiState.Loading
                            },
                        )
                    }
                    UiState.Loading -> {
                        LoadingStateContent(word.text)
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(2000)
                            aiResponse = WordData(
                                originalWord = word.text,
                                translation = "сучасний декларативний UI інструментарій",
                                transcription = "[kəmˈpoʊz]",
                                partOfSpeech = "noun",
                                englishLevel = "B2",
                                examples = listOf(
                                    Example("Compose makes UI development easier.", "Compose робить розробку UI простішою."),
                                    Example("We use Compose for our Android app.", "Ми використовуємо Compose для нашого Android додатку."),
                                    Example("Learning Compose is essential for modern Android development.", "Вивчення Compose є важливим для сучасної розробки Android.")
                                )
                            )
                            uiState = UiState.Success
                        }
                    }
                    is UiState.Success -> {
                        aiResponse?.let { wordData ->
                            TwoSectionContent(
                                wordData = wordData,
                                isMainSectionExpanded = isMainSectionExpanded,
                                onSectionToggle = { isMainSectionExpanded = !isMainSectionExpanded },
                                onAddClick = { /* TODO: Implement add to vocab */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdleStateContent(
    word: TextFieldValue,
    onWordChange: (TextFieldValue) -> Unit,
    onCheckClick: () -> Unit
) {
    OutlinedTextField(
        value = word,
        onValueChange = onWordChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.origin_word)) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            if (word.text.isNotEmpty()) {
                IconButton(onClick = { onWordChange(TextFieldValue("")) }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear text"
                    )
                }
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onCheckClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = stringResource(R.string.add_word_button_text), fontSize = 15.sp)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.description_add_word_dialog),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

@Composable
private fun LoadingStateContent(word: String) {
    OutlinedTextField(
        value = word,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.origin_word)) },
        readOnly = true,
        shape = RoundedCornerShape(12.dp)
    )

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

@Composable
private fun TwoSectionContent(
    wordData: WordData,
    isMainSectionExpanded: Boolean,
    onSectionToggle: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isMainSectionExpanded) onSectionToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMainSectionExpanded)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isMainSectionExpanded) "Основна інформація" else wordData.originalWord,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (!isMainSectionExpanded) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Розгорнути"
                    )
                }
            }

            if (isMainSectionExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = wordData.originalWord,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = wordData.transcription,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = wordData.englishLevel,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = wordData.translation,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = wordData.partOfSpeech,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Секція з прикладами
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (isMainSectionExpanded) onSectionToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!isMainSectionExpanded)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Приклади",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (!isMainSectionExpanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = if (!isMainSectionExpanded) "Згорнути" else "Розгорнути"
                )
            }

            if (!isMainSectionExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                wordData.examples.forEach { example ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = example.english,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = example.ukrainian,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onAddClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = stringResource(R.string.add_to_vocab), fontSize = 15.sp)
    }
}

data class WordData(
    val originalWord: String,
    val translation: String,
    val transcription: String,
    val partOfSpeech: String,
    val englishLevel: String,
    val examples: List<Example>
)

data class Example(
    val english: String,
    val ukrainian: String
)

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
}

@Preview(name = "Add Word Dialog - Idle")
@Composable
fun AddWordDialogPreviewIdle() {
    MaterialTheme {
        AddWordDialog(onDismissRequest = {})
    }
}

@Preview(name = "Add Word Dialog - Success (Examples)")
@Composable
fun AddWordDialogPreviewSuccess() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Compose → сучасний UI інструментарій", modifier = Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = {}) { Icon(painterResource(id = R.drawable.ic_speaker), "") }
                        IconButton(onClick = {}) { Icon(Icons.Default.Close, "") }
                    }
                    Spacer(Modifier.height(16.dp))
                    TwoSectionContent(
                        wordData = WordData(
                            originalWord = "Compose",
                            translation = "сучасний декларативний UI інструментарій",
                            transcription = "[kəmˈpoʊz]",
                            partOfSpeech = "noun",
                            englishLevel = "B2",
                            examples = listOf(
                                Example("Compose makes UI development easier.", "Compose робить розробку UI простішою."),
                                Example("We use Compose for our Android app.", "Ми використовуємо Compose для нашого Android додатку."),
                                Example("Learning Compose is essential for modern Android development.", "Вивчення Compose є важливим для сучасної розробки Android.")
                            )
                        ),
                        isMainSectionExpanded = false,
                        onSectionToggle = {},
                        onAddClick = {}
                    )
                }
            }
        }
    }
}

@Preview(name = "Add Word Dialog - Success (Main Info)")
@Composable
fun AddWordDialogPreviewSuccessMainExpanded() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Add new word", modifier = Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = {}) { Icon(painterResource(id = R.drawable.ic_speaker), "") }
                        IconButton(onClick = {}) { Icon(Icons.Default.Close, "") }
                    }
                    Spacer(Modifier.height(16.dp))
                    TwoSectionContent(
                        wordData = WordData(
                            originalWord = "Compose",
                            translation = "сучасний декларативний UI інструментарій",
                            transcription = "[kəmˈpoʊz]",
                            partOfSpeech = "noun",
                            englishLevel = "B2",
                            examples = listOf(
                                Example("Compose makes UI development easier.", "Compose робить розробку UI простішою."),
                                Example("We use Compose for our Android app.", "Ми використовуємо Compose для нашого Android додатку."),
                                Example("Learning Compose is essential for modern Android development.", "Вивчення Compose є важливим для сучасної розробки Android.")
                            )
                        ),
                        isMainSectionExpanded = true,
                        onSectionToggle = {},
                        onAddClick = {}
                    )
                }
            }
        }
    }
}