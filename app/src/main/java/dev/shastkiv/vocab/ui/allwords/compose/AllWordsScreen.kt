package dev.shastkiv.vocab.ui.allwords.compose

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.AvailableLanguages.findByCode
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.model.Word
import dev.shastkiv.vocab.domain.model.WordData
import dev.shastkiv.vocab.domain.model.enums.WordType
import dev.shastkiv.vocab.ui.addword.compose.components.common.ProBadge
import dev.shastkiv.vocab.ui.addword.compose.components.sections.ExamplesSection
import dev.shastkiv.vocab.ui.addword.compose.components.sections.UsageInfoSection
import dev.shastkiv.vocab.ui.addword.compose.components.sections.WordInfoSection
import dev.shastkiv.vocab.ui.allwords.AllWordsEvent
import dev.shastkiv.vocab.ui.allwords.AllWordsViewModel
import dev.shastkiv.vocab.ui.allwords.AnimationPhase
import dev.shastkiv.vocab.ui.allwords.ExpandedWordState
import dev.shastkiv.vocab.ui.allwords.ScrollPosition
import dev.shastkiv.vocab.ui.allwords.SortType
import dev.shastkiv.vocab.ui.allwords.compose.state.AllWordsUiState
import dev.shastkiv.vocab.ui.common.compose.ErrorContent
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@Composable
fun AllWordsScreen(
    viewModel: AllWordsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    val languageFilter by viewModel.languageFilter.collectAsState()
    val expandedWordState by viewModel.expandedWordState.collectAsState()
    val savedScrollPosition by viewModel.savedScrollPosition.collectAsState()

    AllWordsContent(
        uiState = uiState,
        searchQuery = searchQuery,
        sortType = sortType,
        languageFilter = languageFilter,
        availableLanguages = viewModel.availableLanguages,
        expandedWordState = expandedWordState,
        onEvent = viewModel::onEvent,
        onSaveScrollPosition = viewModel::saveScrollPosition,
        onBackPressed = onBackPressed,
        savedScrollPosition = savedScrollPosition
    )
}

@Composable
private fun AllWordsContent(
    uiState: AllWordsUiState,
    searchQuery: String,
    sortType: SortType,
    languageFilter: String,
    availableLanguages: List<Language>,
    expandedWordState: ExpandedWordState?,
    onEvent: (AllWordsEvent) -> Unit,
    onSaveScrollPosition: (Int, Int) -> Unit,
    onBackPressed: () -> Unit,
    savedScrollPosition: ScrollPosition
) {
    val dimensions = MaterialTheme.appDimensions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(dimensions.mediumPadding)
    ) {
        AllWordsHeader(
            languageFilter = languageFilter,
            sortType = sortType,
            availableLanguages = availableLanguages,
            onEvent = onEvent,
            onBackPressed = onBackPressed
        )

        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChanged = { onEvent(AllWordsEvent.SearchQueryChanged(it)) }
        )

        AllWordsMainContent(
            uiState = uiState,
            searchQuery = searchQuery,
            expandedWordState = expandedWordState,
            onEvent = onEvent,
            onSaveScrollPosition = onSaveScrollPosition,
            savedScrollPosition = savedScrollPosition
        )
    }
}

@Composable
private fun AllWordsHeader(
    languageFilter: String,
    sortType: SortType,
    availableLanguages: List<Language>,
    onEvent: (AllWordsEvent) -> Unit,
    onBackPressed: () -> Unit
) {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val dimensions = MaterialTheme.appDimensions

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Navigate back",
            tint = colors.cardTitleText,
            modifier = Modifier
                .size(dimensions.headerIconSize)
                .clickable { onBackPressed() }
        )

        Text(
            text = stringResource(R.string.my_dictionary_title),
            style = typography.header,
            color = colors.cardTitleText,
            modifier = Modifier.weight(1f)
        )

        LanguageFilterMenu(
            availableLanguages = availableLanguages,
            currentLanguageCode = languageFilter,
            onLanguageSelected = { onEvent(AllWordsEvent.LanguageFilterChanged(it)) }
        )

        SortMenu(
            currentSortType = sortType,
            onSortSelected = { onEvent(AllWordsEvent.SortTypeChanged(it)) }
        )
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    val dimensions = MaterialTheme.appDimensions

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.mediumPadding),
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        shape = RoundedCornerShape(dimensions.mediumCornerRadius)
    )
}

@Composable
private fun ColumnScope.AllWordsMainContent(
    uiState: AllWordsUiState,
    searchQuery: String,
    expandedWordState: ExpandedWordState?,
    onEvent: (AllWordsEvent) -> Unit,
    onSaveScrollPosition: (Int, Int) -> Unit,
    savedScrollPosition: ScrollPosition
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        shape = RoundedCornerShape(dimensions.largeCornerRadius),
        color = colors.cardBackground,
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Crossfade(
            targetState = uiState,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            ),
            label = "StateTransition"
        ) { state ->
            when (state) {
                is AllWordsUiState.Success -> {
                    if (state.words.isEmpty()) {
                        EmptyState(
                            message = if (searchQuery.isNotBlank())
                                stringResource(R.string.error_search_description)
                            else
                                stringResource(R.string.error_empty_language_filter_description)
                        )
                    } else {
                        WordsList(
                            words = state.words,
                            expandedWordState = expandedWordState,
                            onEvent = onEvent,
                            onSaveScrollPosition = onSaveScrollPosition,
                            savedScrollPosition = savedScrollPosition
                        )
                    }
                }
                is AllWordsUiState.Loading -> LoadingState()
                is AllWordsUiState.Error -> {
                    ErrorContent(
                        error = state.error,
                        onRetry = {},
                        modifier = Modifier.fillMaxSize(),
                        showButton = false
                    )
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun WordsList(
    words: List<Word>,
    expandedWordState: ExpandedWordState?,
    onEvent: (AllWordsEvent) -> Unit,
    onSaveScrollPosition: (Int, Int) -> Unit,
    savedScrollPosition: ScrollPosition
) {
    val listState = rememberLazyListState()

    LaunchedEffect(expandedWordState?.animationPhase) {
        val wordId = expandedWordState?.wordId
        when (expandedWordState?.animationPhase) {
            AnimationPhase.MOVING_TO_TOP -> {
                if (wordId != null) {
                    val targetIndex = words.indexOfFirst { it.id == wordId }
                    if (targetIndex != -1) {
                        onSaveScrollPosition(
                            listState.firstVisibleItemIndex,
                            listState.firstVisibleItemScrollOffset
                        )
                        yield()

                        listState.animateScrollToItem(index = targetIndex, scrollOffset = 0)
                    }
                }
            }
            AnimationPhase.MOVING_TO_ORIGINAL -> {
                delay(50)
                listState.animateScrollToItem(
                    index = savedScrollPosition.index,
                    scrollOffset = savedScrollPosition.offset
                )
            }
            else -> {}
        }
    }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val targetIndex = remember(expandedWordState?.wordId) {
        words.indexOfFirst { it.id == expandedWordState?.wordId }
    }


    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            bottom = if (expandedWordState != null) screenHeight else 0.dp
        ),
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = expandedWordState == null
    ) {
        itemsIndexed(
            items = words,
            key = { _, word -> word.id }
        ) { index, word ->
            WordListItem(
                word = word,
                index = index,
                targetIndex = targetIndex,
                expandedWordState = expandedWordState,
                onEvent = onEvent,
                listState = listState
            )
        }
    }
}

@Composable
private fun WordListItem(
    word: Word,
    index: Int,
    targetIndex: Int,
    expandedWordState: ExpandedWordState?,
    onEvent: (AllWordsEvent) -> Unit,
    listState: LazyListState
) {
    val appColors = MaterialTheme.appColors

    val isAnyFocused = expandedWordState != null
    val isThisWordFocused = expandedWordState?.wordId == word.id
    val phase = expandedWordState?.animationPhase ?: AnimationPhase.IDLE

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isAnyFocused && !isThisWordFocused) 0.0f else 1f,
        animationSpec = tween(400)
    )
    val backgroundScaleX by animateFloatAsState(
        targetValue = if (isAnyFocused && !isThisWordFocused) 0.97f else 1f,
        animationSpec = tween(400)
    )

    val backgroundScaleY by animateFloatAsState(
        targetValue = if (isAnyFocused && !isThisWordFocused) 0.94f else 1f,
        animationSpec = tween(400)
    )

    val translationY by animateFloatAsState(
        targetValue = when {
            !isAnyFocused || isThisWordFocused -> 0f
            index < targetIndex -> -50f
            else -> 50f
        },
        animationSpec = tween(400)
    )

    val itemAlpha = remember { Animatable(0f) }
    val itemOffset = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        val relativeIndex = (index - listState.firstVisibleItemIndex).coerceIn(0, 5)

        delay(relativeIndex * 40L)

        launch { itemAlpha.animateTo(1f, tween(400, easing = LinearOutSlowInEasing)) }
        launch { itemOffset.animateTo(0f, tween(400, easing = LinearOutSlowInEasing)) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = backgroundAlpha * itemAlpha.value
                this.translationY = translationY + itemOffset.value
                scaleX = backgroundScaleX
                scaleY = backgroundScaleY
            }
    ) {
        WordRow(
            word = word,
            isExpanded = isThisWordFocused,
            animationPhase = phase,
            onClick = {
                if (!isAnyFocused || isThisWordFocused) {
                    onEvent(AllWordsEvent.WordClicked(word))
                }
            }
        )

        AnimatedVisibility(
            visible = isThisWordFocused,
            enter = expandVertically(animationSpec = tween(400)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(400)) + fadeOut()
        ) {
            WordDetailsSection(
                expandedWordState = expandedWordState,
                animationPhase = phase
            )
        }

        if (!isAnyFocused) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = appColors.cardBorder
            )
        }
    }
}

@Composable
private fun WordRow(
    word: Word,
    isExpanded: Boolean,
    animationPhase: AnimationPhase,
    onClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    val iconRotation by animateFloatAsState(
        targetValue = when {
            isExpanded && animationPhase in listOf(
                AnimationPhase.SHOWING_DETAILS,
                AnimationPhase.EXPANDED
            ) -> 180f
            else -> 0f
        },
        animationSpec = tween(
            durationMillis = AllWordsViewModel.Companion.SHOW_DETAILS_DURATION.toInt(),
            easing = FastOutSlowInEasing
        ),
        label = "iconRotation"
    )

    val transitionProgress by animateFloatAsState(
        targetValue = if (isExpanded && animationPhase in listOf(
                AnimationPhase.HIDING_OTHERS,
                AnimationPhase.MOVING_TO_TOP,
                AnimationPhase.SHOWING_DETAILS,
                AnimationPhase.EXPANDED,
                AnimationPhase.HIDING_DETAILS
            )) 1f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "textScaleProgress"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(dimensions.mediumPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing)
        ) {
            val animatedStyle = lerp(
                start = typography.wordHeadLine,
                stop = typography.header,
                fraction = transitionProgress
            )

            Text(
                text = word.sourceWord,
                style = animatedStyle,
                fontWeight = FontWeight.SemiBold,
                color = colors.cardTitleText
            )

            Text(
                text = findByCode(word.sourceLanguageCode).flagEmoji,
                style = typography.cardTitleMedium
            )
        }

        if (word.wordType == WordType.PRO && !isExpanded) {
            ProBadge()
            Spacer(modifier = Modifier.width(dimensions.smallSpacing))
        }

        Icon(
            imageVector = if (animationPhase in listOf(
                    AnimationPhase.SHOWING_DETAILS,
                    AnimationPhase.EXPANDED
                )
            ) Icons.Default.Close else Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.rotate(iconRotation),
            tint = colors.cardArrowTint
        )
    }
}

@Composable
private fun WordDetailsSection(
    expandedWordState: ExpandedWordState?,
    animationPhase: AnimationPhase
) {
    val dimensions = MaterialTheme.appDimensions

    val detailsVisible = animationPhase in listOf(
        AnimationPhase.SHOWING_DETAILS,
        AnimationPhase.EXPANDED
    )

    AnimatedVisibility(
        visible = detailsVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = AllWordsViewModel.Companion.SHOW_DETAILS_DURATION.toInt(),
                easing = FastOutSlowInEasing
            )
        ) + expandVertically(
            animationSpec = tween(
                durationMillis = AllWordsViewModel.Companion.SHOW_DETAILS_DURATION.toInt(),
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = AllWordsViewModel.Companion.SHOW_DETAILS_DURATION.toInt(),
                easing = FastOutSlowInEasing
            )
        ) + shrinkVertically(
            animationSpec = tween(
                durationMillis = AllWordsViewModel.Companion.SHOW_DETAILS_DURATION.toInt(),
                easing = FastOutSlowInEasing
            )
        )
    ) {
        if (expandedWordState?.details != null) {
            WordDetailsContent(expandedWordState.details)
        } else if (expandedWordState?.isLoading == true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.mediumPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun WordDetailsContent(detailsResult: Result<WordData>) {
    val dimensions = MaterialTheme.appDimensions
    var activeSection by remember { mutableStateOf(ExpandedSection.WORD_INFO) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.mediumPadding)
            .padding(bottom = dimensions.mediumPadding),
        verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
    ) {
        detailsResult.onSuccess { wordData ->
            WordInfoSection(
                isExpanded = activeSection == ExpandedSection.WORD_INFO,
                onToggle = {
                    activeSection = if (activeSection == ExpandedSection.WORD_INFO)
                        ExpandedSection.NONE else ExpandedSection.WORD_INFO
                },
                wordData = wordData,
                originalWord = wordData.originalWord,
                translation = wordData.translation,
                isLocked = false
            )

            ExamplesSection(
                isExpanded = activeSection == ExpandedSection.EXAMPLES,
                onToggle = {
                    activeSection = if (activeSection == ExpandedSection.EXAMPLES)
                        ExpandedSection.NONE else ExpandedSection.EXAMPLES
                },
                isLocked = false,
                examples = wordData.examples
            )

            UsageInfoSection(
                isExpanded = activeSection == ExpandedSection.USAGE,
                onToggle = {
                    activeSection = if (activeSection == ExpandedSection.USAGE)
                        ExpandedSection.NONE else ExpandedSection.USAGE
                },
                isLocked = false,
                context = wordData.usageInfo
            )
        }
    }
}

enum class ExpandedSection {
    WORD_INFO, EXAMPLES, USAGE, NONE
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.loading_list)
        )
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.error)
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
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

@Composable
private fun LanguageFilterMenu(
    availableLanguages: List<Language>,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = MaterialTheme.appColors.accent
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_all_languages)) },
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
private fun SortMenu(
    currentSortType: SortType,
    onSortSelected: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Sort",
                tint = MaterialTheme.appColors.accent
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sort_newest)) },
                onClick = {
                    onSortSelected(SortType.BY_DATE_NEWEST)
                    expanded = false
                },
                enabled = currentSortType != SortType.BY_DATE_NEWEST
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sort_oldest)) },
                onClick = {
                    onSortSelected(SortType.BY_DATE_OLDEST)
                    expanded = false
                },
                enabled = currentSortType != SortType.BY_DATE_OLDEST
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sort_az)) },
                onClick = {
                    onSortSelected(SortType.ALPHABETICALLY_AZ)
                    expanded = false
                },
                enabled = currentSortType != SortType.ALPHABETICALLY_AZ
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.sort_za)) },
                onClick = {
                    onSortSelected(SortType.ALPHABETICALLY_ZA)
                    expanded = false
                },
                enabled = currentSortType != SortType.ALPHABETICALLY_ZA
            )
        }
    }
}