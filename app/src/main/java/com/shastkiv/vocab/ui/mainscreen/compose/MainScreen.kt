package com.shastkiv.vocab.ui.mainscreen.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.languageapp.UserStatsCard
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.navigation.Screen
import com.shastkiv.vocab.ui.common.compose.ErrorContent
import com.shastkiv.vocab.ui.mainscreen.MainViewModel
import com.shastkiv.vocab.ui.mainscreen.compose.components.AddWordCard
import com.shastkiv.vocab.ui.mainscreen.compose.components.AllWordsCard
import com.shastkiv.vocab.ui.mainscreen.compose.components.PracticeCard
import com.shastkiv.vocab.ui.mainscreen.compose.components.QuizCard
import com.shastkiv.vocab.ui.theme.customColors

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
private fun MainScreenHeader(
    onSettingsClick: () -> Unit,
    languageSettings: LanguageSettings
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderTitle(languageSettings = languageSettings)
        SettingsButton(onClick = onSettingsClick)
    }
}

@Composable
private fun HeaderTitle(languageSettings: LanguageSettings) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.customColors.cardTitleText
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${languageSettings.sourceLanguage.flagEmoji} -> ${languageSettings.targetLanguage.flagEmoji}",
                color = MaterialTheme.customColors.cardTitleText,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.description_head_text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.customColors.cardDescriptionText
        )
    }
}

@Composable
private fun SettingsButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick ,
        modifier = Modifier
            .statusBarsPadding()
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.customColors.cardBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.customColors.cardBorder,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Switch to light mode",
            tint = Color(0xFF5EEAD4),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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

    ActionSection(
        title = stringResource(R.string.head_text),
        onAddWordClick = onAddWordClick,
        onRepetitionClick = onRepetitionClick,
        onAllWordsClick = onAllWordsClick,
        onPracticeClick = onPracticeClick
    )
}

@Composable
private fun ActionSection(
    title: String,
    onAddWordClick: () -> Unit,
    onRepetitionClick: () -> Unit,
    onAllWordsClick: () -> Unit,
    onPracticeClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {

        SectionHeader(title = title)
        Column (
            modifier = Modifier
        ) {
            AddWordCard(onClick = onAddWordClick)
            HorizontalDivider(modifier = Modifier.size(10.dp))
            QuizCard(onClick = onRepetitionClick)
            HorizontalDivider(modifier = Modifier.size(10.dp))
            AllWordsCard(onClick = onAllWordsClick)
            HorizontalDivider(modifier = Modifier.size(10.dp))
            PracticeCard(onClick = onPracticeClick)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(16.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_right_chevron),
            contentDescription = null
        )
    }
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