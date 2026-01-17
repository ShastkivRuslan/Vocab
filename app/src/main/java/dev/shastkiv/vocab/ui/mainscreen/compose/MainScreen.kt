package dev.shastkiv.vocab.ui.mainscreen.compose

import ActionSection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import dev.example.languageapp.UserStatsCard
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.domain.model.LanguageSettings
import dev.shastkiv.vocab.navigation.Screen
import dev.shastkiv.vocab.ui.common.compose.ErrorContent
import dev.shastkiv.vocab.ui.mainscreen.MainViewModel
import dev.shastkiv.vocab.ui.mainscreen.compose.components.LoadingContent
import dev.shastkiv.vocab.ui.mainscreen.compose.components.MainScreenHeader
import dev.shastkiv.vocab.ui.mainscreen.compose.components.VocabPromoCard
import dev.shastkiv.vocab.ui.mainscreen.compose.components.WidgetPromoCard
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.utils.WidgetManager
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    val statisticsState by viewModel.statisticsState.collectAsState()
    val languageSettings by viewModel.languageSettings.collectAsState()
    val showWidgetPromo by viewModel.showWidgetPromo.collectAsState()
    val showVocabPromo by viewModel.showVocabPromo.collectAsState()
    val context = LocalContext.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.updateWidgetStatus(context)
        viewModel.updateVocabPlusStatus(context)
    }

    MainScreenLayout(
        modifier = modifier,
        statisticsState = statisticsState,
        languageSettings = languageSettings,
        onSettingsClick = { navController.navigate(Screen.Settings.route) },
        onAddWordClick = { navController.navigate(Screen.AddWord.route) },
        onRepetitionClick = { navController.navigate(Screen.Repetition.route) },
        onAllWordsClick = { navController.navigate(Screen.AllWords.route) },
        onPracticeClick = { navController.navigate(Screen.Practice.route) },
        showWidgetPromo = showWidgetPromo,
        onAddWidgetClick = { WidgetManager.requestPinWidget(context) },
        showVocabPromo = showVocabPromo,
        onEnableVocabClick = { navController.navigate(Screen.BubbleSettings.route) }
    )
}

@Composable
fun MainScreenLayout(
    modifier: Modifier,
    statisticsState: MainViewModel.StatisticsUiState,
    languageSettings: LanguageSettings,
    onSettingsClick: () -> Unit,
    onAddWordClick: () -> Unit,
    onRepetitionClick: () -> Unit,
    onAllWordsClick: () -> Unit,
    onPracticeClick: () -> Unit,
    showWidgetPromo: Boolean,
    onAddWidgetClick: () -> Unit,
    showVocabPromo: Boolean,
    onEnableVocabClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        MainScreenHeader(
            onSettingsClick = onSettingsClick,
            languageSettings = languageSettings
        )

        when {
            statisticsState.isLoading -> LoadingContent()
            statisticsState.error != null -> {
                ErrorContent(
                    error = statisticsState.error,
                    onRetry = { }
                )
            }
            else -> MainContent(
                totalWords = statisticsState.totalWordsCount,
                notLearnedWords = statisticsState.notLearnedWordsCount,
                todayWordsCount = statisticsState.todayWordsCount,
                learnedPercentage = statisticsState.learnedPercentage,
                onAddWordClick = onAddWordClick,
                onRepetitionClick = onRepetitionClick,
                onAllWordsClick = onAllWordsClick,
                onPracticeClick = onPracticeClick,
                showWidgetPromo = showWidgetPromo,
                onAddWidgetClick = onAddWidgetClick,
                showVocabPromo = showVocabPromo,
                onEnableVocabClick = onEnableVocabClick
            )
        }
    }
}

@Composable
private fun MainContent(
    totalWords: Int,
    notLearnedWords: Int,
    todayWordsCount: Int,
    learnedPercentage: Int,
    onAddWordClick: () -> Unit,
    onRepetitionClick: () -> Unit,
    onAllWordsClick: () -> Unit,
    onPracticeClick: () -> Unit,
    showWidgetPromo: Boolean,
    onAddWidgetClick: () -> Unit,
    showVocabPromo: Boolean,
    onEnableVocabClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions

    UserStatsCard(totalWords, notLearnedWords, todayWordsCount, learnedPercentage)

    val promoPages = remember(showWidgetPromo, showVocabPromo) {
        val list = mutableListOf<@Composable () -> Unit>()
        if (showWidgetPromo) list.add { WidgetPromoCard(onClick = onAddWidgetClick) }
        if (showVocabPromo) list.add { VocabPromoCard(onClick = onEnableVocabClick) }
        list
    }

    AnimatedVisibility(
        visible = promoPages.isNotEmpty(),
        enter = fadeIn(animationSpec = tween(500)) +
                expandVertically(expandFrom = Alignment.Top, animationSpec = tween(600)) +
                scaleIn(initialScale = 0.9f, animationSpec = tween(500))
    ) {
        val pagerState = rememberPagerState(pageCount = { promoPages.size })

        LaunchedEffect(promoPages.size) {
            while (promoPages.size > 1) {
                delay(3000)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % promoPages.size
                    pagerState.animateScrollToPage(
                        page = nextPage,
                        animationSpec = tween(
                            durationMillis = 800,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth(),
                pageSpacing = dimensions.smallSpacing
            ) { page ->
                promoPages[page]()
            }

            if (promoPages.size > 1) {
                Row(
                    Modifier.height(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(promoPages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration)
                            MaterialTheme.appColors.textMain
                        else
                            MaterialTheme.appColors.textMain.copy(alpha = 0.3f)

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(6.dp)
                        )
                    }
                }
            }
        }
    }

    ActionSection(
        title = stringResource(R.string.head_text),
        onAddWordClick = onAddWordClick,
        onRepetitionClick = onRepetitionClick,
        onAllWordsClick = onAllWordsClick,
        onPracticeClick = onPracticeClick
    )
}


@Preview(name = "Main Screen - Normal")
@Composable
fun MainScreenPreview() {
    MainScreenLayout(
        modifier = Modifier,
        statisticsState = MainViewModel.StatisticsUiState(
            totalWordsCount = 120,
            learnedPercentage = 45,
            isLoading = false
        ),
        onSettingsClick = {},
        onAddWordClick = {},
        onRepetitionClick = {},
        onAllWordsClick = {},
        onPracticeClick = {},
        languageSettings = LanguageSettings(
            appLanguage = Language("uk", "Українська", "🇺🇦"),
            targetLanguage = Language("pl", "Polski", "🇵🇱"),
            sourceLanguage = Language("en", "English", "🇬🇧")
        ),
        showWidgetPromo = true,
        onAddWidgetClick = {},
        showVocabPromo = true,
        onEnableVocabClick = {}
    )
}

@Preview(name = "Main Screen - Loading")
@Composable
fun MainScreenLoadingPreview() {
    MainScreenLayout(
        modifier = Modifier,
        statisticsState = MainViewModel.StatisticsUiState(isLoading = true),
        onSettingsClick = {},
        onAddWordClick = {},
        onRepetitionClick = {},
        onAllWordsClick = {},
        onPracticeClick = {},
        languageSettings = LanguageSettings(
            appLanguage = Language("uk", "Українська", "🇺🇦"),
            targetLanguage = Language("uk", "Українська", "🇺🇦"),
            sourceLanguage = Language("en", "English", "🇬🇧")
        ),
        showWidgetPromo = true,
        onAddWidgetClick = {},
        showVocabPromo = true,
        onEnableVocabClick = {}
    )
}