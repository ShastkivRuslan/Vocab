package com.shastkiv.vocab.ui.allwords

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.AvailableLanguages.findByCode
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.Word
import com.shastkiv.vocab.domain.model.WordData
import com.shastkiv.vocab.domain.model.WordType
import com.shastkiv.vocab.ui.addword.compose.components.common.ProBadge
import com.shastkiv.vocab.ui.allwords.compose.components.ExamplesItem
import com.shastkiv.vocab.ui.allwords.compose.components.InfoItem
import com.shastkiv.vocab.ui.allwords.compose.components.MainInfoItem
import com.shastkiv.vocab.ui.allwords.compose.state.AllWordsUiState
import com.shastkiv.vocab.ui.common.compose.ErrorContent
import com.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme

@Composable
fun AllWordsScreen(
    viewModel: AllWordsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val languageFilter by viewModel.languageFilter.collectAsState()
    val availableLanguages = viewModel.availableLanguages
    val expandedWordId by viewModel.expandedWordId.collectAsState()
    val expandedWordDetails by viewModel.expandedWordDetails.collectAsState()
    val isDetailsLoading by viewModel.isDetailsLoading.collectAsState()

    AllWordsContent(
        uiState = uiState,
        searchQuery = searchQuery,
        sortType = sortType,
        languageFilter = languageFilter,
        availableLanguages = availableLanguages,
        expandedWordId = expandedWordId,
        expandedWordDetails = expandedWordDetails,
        isDetailsLoading = isDetailsLoading,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSortChanged = viewModel::onSortChanged,
        onLanguageFilterChanged = viewModel::onLanguageFilterChanged,
        onWordClick = viewModel::onWordClicked,
        onBackPressed = onBackPressed
    )
}

@Composable
fun AllWordsContent(
    uiState: AllWordsUiState,
    searchQuery: String,
    sortType: SortType,
    languageFilter: String,
    availableLanguages: List<Language>,
    expandedWordId: Int?,
    expandedWordDetails: Result<WordData>?,
    isDetailsLoading: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSortChanged: (SortType) -> Unit,
    onLanguageFilterChanged: (String) -> Unit,
    onWordClick: (Word) -> Unit,
    onBackPressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = "Мій словник",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f)
            )
            if (uiState is AllWordsUiState.Success) {
                LanguageFilterMenu(
                    availableLanguages = availableLanguages,
                    currentLanguageCode = languageFilter,
                    onLanguageSelected = onLanguageFilterChanged
                )
            }
            SortMenu(
                currentSortType = sortType,
                onSortSelected = onSortChanged
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is AllWordsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AllWordsUiState.Error -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = { /* reload */ },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is AllWordsUiState.Success -> {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                        placeholder = { Text("Пошук у словнику...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                        shape = MaterialTheme.shapes.extraLarge
                    )

                    if (uiState.words.isEmpty()) {
                        EmptyState(message = if (searchQuery.isNotBlank()) "Слів за вашим запитом не знайдено" else "Слів для обраної мови немає.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.words, key = { it.id }) { word ->
                                WordItem(
                                    word = word,
                                    isExpanded = word.id == expandedWordId,
                                    detailsResult = if (word.id == expandedWordId) expandedWordDetails else null,
                                    isLoading = isDetailsLoading && (word.id == expandedWordId),
                                    isPro = word.wordType == WordType.PRO,
                                    onClick = { onWordClick(word) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FreeWordContent(word: Word) {
    Column {
        Text(
            text = "Переклад:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = word.translation,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "*Оновіться до PRO для детальної інформації про слово",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun WordItem(
    word: Word,
    isExpanded: Boolean,
    detailsResult: Result<WordData>?,
    isLoading: Boolean,
    isPro: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${word.sourceWord}  ${findByCode(word.sourceLanguageCode).flagEmoji}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isPro) {
                    ProBadge()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Згорнути" else "Розгорнути"
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    detailsResult != null -> {
                        detailsResult.onSuccess { wordData ->
                            if (isPro) {
                                // PRO слово - повні деталі
                                ProWordDetailsContent(wordData = wordData)
                            } else {
                                // FREE слово - простий переклад
                                FreeWordContent(word = word)
                            }
                        }.onFailure {
                            Text(
                                "Не вдалося завантажити деталі.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProWordDetailsContent(wordData: WordData) {

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MainInfoItem(word = wordData)
        ExamplesItem(examples = wordData.examples)
        InfoItem(word = wordData)
    }
}

@Composable
fun LanguageFilterMenu(
    availableLanguages: List<Language>,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter by language",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("🌍 Всі мови") },
                onClick = {
                    onLanguageSelected("all")
                    expanded = false
                },
                enabled = currentLanguageCode != "all"
            )
            availableLanguages.forEach { lang ->
                DropdownMenuItem(
                    text = { Text("${lang.flagEmoji} ${lang.name}") },
                    onClick = {
                        onLanguageSelected(lang.code)
                        expanded = false
                    },
                    enabled = currentLanguageCode != lang.code
                )
            }
        }
    }
}


@Composable
fun SortMenu(
    currentSortType: SortType,
    onSortSelected: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sort words",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Спочатку нові") },
                onClick = {
                    onSortSelected(SortType.BY_DATE_NEWEST)
                    expanded = false
                },
                enabled = currentSortType != SortType.BY_DATE_NEWEST
            )
            DropdownMenuItem(
                text = { Text("Спочатку старі") },
                onClick = {
                    onSortSelected(SortType.BY_DATE_OLDEST)
                    expanded = false
                },
                enabled = currentSortType != SortType.BY_DATE_OLDEST
            )
            DropdownMenuItem(
                text = { Text("За алфавітом (А-Я)") },
                onClick = {
                    onSortSelected(SortType.ALPHABETICALLY_AZ)
                    expanded = false
                },
                enabled = currentSortType != SortType.ALPHABETICALLY_AZ
            )
            DropdownMenuItem(
                text = { Text("За алфавітом (Я-А)") },
                onClick = {
                    onSortSelected(SortType.ALPHABETICALLY_ZA)
                    expanded = false
                },
                enabled = currentSortType != SortType.ALPHABETICALLY_ZA
            )
        }
    }
}


@Composable
fun EmptyState(message: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AllWordsScreenPreview() {
    val mockWords = listOf(
        Word(
            id = 1,
            sourceWord = "Apple",
            translation = "Яблуко",
            sourceLanguageCode = "en",
            targetLanguageCode = "uk",
            wordType = WordType.FREE,
            isWordAdded = true
        ),
        Word(
            id = 2,
            sourceWord = "Banana",
            translation = "Банан",
            sourceLanguageCode = "en",
            targetLanguageCode = "uk",
            wordType = WordType.PRO,
            isWordAdded = true
        )
    )
    LearnWordsTrainerTheme(darkTheme = true) {
        Surface {
            AllWordsContent(
                uiState = AllWordsUiState.Success(mockWords),
                searchQuery = "",
                sortType = SortType.BY_DATE_NEWEST,
                languageFilter = "en",
                availableLanguages = AllWordsViewModel.AVAILABLE_LANGUAGES,
                expandedWordId = 1,
                expandedWordDetails = Result.success(WordData("Banana", "Банан", "[bəˈnɑːnə]", "noun", "A1", "Синоніми: fruit", emptyList())),
                isDetailsLoading = false,
                onSearchQueryChanged = {},
                onSortChanged = {},
                onLanguageFilterChanged = {},
                onWordClick = {},
                onBackPressed = {}
            )
        }
    }
}