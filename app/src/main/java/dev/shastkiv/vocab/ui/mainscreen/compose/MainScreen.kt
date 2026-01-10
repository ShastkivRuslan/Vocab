package dev.shastkiv.vocab.ui.mainscreen.compose

import ActionSection
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import dev.shastkiv.vocab.ui.mainscreen.compose.components.WidgetPromoCard

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController
) {
    val statisticsState by viewModel.statisticsState.collectAsState()
    val languageSettings by viewModel.languageSettings.collectAsState()

    MainScreenLayout(
        modifier = modifier,
        statisticsState = statisticsState,
        languageSettings = languageSettings,
        onSettingsClick = { navController.navigate(Screen.Settings.route) },
        onAddWordClick = { navController.navigate(Screen.AddWord.route) },
        onRepetitionClick = { navController.navigate(Screen.Repetition.route) },
        onAllWordsClick = { navController.navigate(Screen.AllWords.route) },
        onPracticeClick = { navController.navigate(Screen.Practice.route) }
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
    onPracticeClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
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
                onPracticeClick = onPracticeClick
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
    onPracticeClick: () -> Unit
) {
    UserStatsCard(totalWords, notLearnedWords, todayWordsCount, learnedPercentage)

    WidgetPromoCard({})

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
        )
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
        )
    )
}