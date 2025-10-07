package com.shastkiv.vocab.ui.mainscreen.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.domain.model.LanguageSettings
import com.shastkiv.vocab.navigation.Screen
import com.shastkiv.vocab.ui.common.compose.ErrorContent
import com.shastkiv.vocab.ui.mainscreen.MainViewModel
import com.shastkiv.vocab.ui.mainscreen.compose.components.AddWordCard
import com.shastkiv.vocab.ui.mainscreen.compose.components.AllWordsCard
import com.shastkiv.vocab.ui.mainscreen.compose.components.PracticeCard
import com.shastkiv.vocab.ui.mainscreen.compose.components.RepetitionCard

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
        reloadStatistic = { viewModel.reloadStatistics() },
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
    reloadStatistic: () -> Unit,
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
                    onRetry = { reloadStatistic() }
                )
            }
            else -> MainContent(
                totalWords = statisticsState.totalWordsCount,
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
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${languageSettings.sourceLanguage.flagEmoji} -> ${languageSettings.targetLanguage.flagEmoji}",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.description_head_text),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun SettingsButton(onClick: () -> Unit) {
    Card(modifier = Modifier.size(64.dp)) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings_screen_title),
                tint = MaterialTheme.colorScheme.primary
            )
        }
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
    learnedPercentage: Int,
    onAddWordClick: () -> Unit,
    onRepetitionClick: () -> Unit,
    onAllWordsClick: () -> Unit,
    onPracticeClick: () -> Unit
) {
    StatisticsCard(totalWords, learnedPercentage)
    ActionSection(
        title = stringResource(R.string.head_text),
        onAddWordClick = onAddWordClick,
        onRepetitionClick = onRepetitionClick,
        onAllWordsClick = onAllWordsClick
    )
    PracticeSection(
        title = stringResource(R.string.practice_mode),
        onPracticeClick = onPracticeClick
    )
}

@Composable
private fun StatisticsCard(totalWords: Int, learnedPercentage: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatItem(
                label = stringResource(R.string.count_words),
                value = totalWords.toString(),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = stringResource(R.string.learned),
                value = stringResource(R.string.percentage_format, learnedPercentage),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActionSection(
    title: String,
    onAddWordClick: () -> Unit,
    onRepetitionClick: () -> Unit,
    onAllWordsClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = title)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            AddWordCard(onClick = onAddWordClick, modifier = Modifier.padding(end = 16.dp))
            RepetitionCard(onClick = onRepetitionClick, modifier = Modifier.padding(end = 16.dp))
            AllWordsCard(onClick = onAllWordsClick)
        }
    }
}

@Composable
private fun PracticeSection(title: String, onPracticeClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = title)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
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
        reloadStatistic = {},
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
        reloadStatistic = {},
        languageSettings = LanguageSettings(
            appLanguage = Language("uk", "Українська", "🇺🇦"),
            targetLanguage = Language("uk", "Українська", "🇺🇦"),
            sourceLanguage = Language("en", "English", "🇬🇧")
        )
    )
}