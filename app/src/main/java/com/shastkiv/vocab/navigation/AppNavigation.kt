package com.shastkiv.vocab.navigation

import AddWordScreen
import BubbleSettingsScreen
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.ui.SplashScreen
import com.shastkiv.vocab.ui.about.button.AboutButtonScreen
import com.shastkiv.vocab.ui.addword.shared.AddWordViewModelProvider
import com.shastkiv.vocab.ui.allwords.AllWordsScreen
import com.shastkiv.vocab.ui.common.compose.FeatureInDevelopScreen
import com.shastkiv.vocab.ui.initialsetup.AppStartViewModel
import com.shastkiv.vocab.ui.initialsetup.InitialSetupViewModel
import com.shastkiv.vocab.ui.initialsetup.compose.InitialSetupScreen
import com.shastkiv.vocab.ui.mainscreen.MainViewModel
import com.shastkiv.vocab.ui.mainscreen.compose.MainScreen
import com.shastkiv.vocab.ui.practice.compose.PracticeScreen
import com.shastkiv.vocab.ui.repetition.RepetitionViewModel
import com.shastkiv.vocab.ui.repetition.compose.RepetitionScreen
import com.shastkiv.vocab.ui.settings.bubble.BubbleSettingsViewModel
import com.shastkiv.vocab.ui.settings.language.LanguageDialogType
import com.shastkiv.vocab.ui.settings.language.LanguageSettingsViewModel
import com.shastkiv.vocab.ui.settings.language.compose.LanguageSelectionBottomSheet
import com.shastkiv.vocab.ui.settings.language.compose.LanguageSettingsScreen
import com.shastkiv.vocab.ui.settings.main.SettingsViewModel
import com.shastkiv.vocab.ui.settings.main.compose.SettingsScreen
import com.shastkiv.vocab.ui.settings.main.compose.ThemeSelectionBottomSheet
import com.shastkiv.vocab.ui.settings.notification.NotificationSettingsScreen
import com.shastkiv.vocab.ui.settings.notification.NotificationSettingsViewModel
import com.shastkiv.vocab.ui.theme.appGradientColors
import kotlinx.coroutines.delay

private const val NAVIGATION_ANIMATION_DURATION = 400
private const val SPLASH_SCREEN_DURATION = 1300L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val gradientColors = MaterialTheme.appGradientColors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        gradientColors.color1,
                        gradientColors.color2,
                        gradientColors.color3
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors =
                            listOf(
                                gradientColors.color4,
                                Color.Transparent
                            ),
                        radius = 800f
                    )
                )
        )

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION)) +
                        slideInVertically(
                            initialOffsetY = { it / 20 },
                            animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION / 2))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION)) +
                        slideOutVertically(
                            targetOffsetY = { it / 20 },
                            animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION)
                        )
            }
        ) {
            // Splash Screen Route
            composable(route = Screen.Splash.route) {
                val appStartViewModel: AppStartViewModel = hiltViewModel()
                val isSetupCompleted by appStartViewModel.isSetupCompleted.collectAsState()

                LaunchedEffect(isSetupCompleted) {
                    if (isSetupCompleted != null) {
                        delay(SPLASH_SCREEN_DURATION)

                        val destination = if (isSetupCompleted == true) {
                            Screen.Main.route
                        } else {
                            Screen.Onboarding.route
                        }

                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }

                SplashScreen()
            }
            composable(route = Screen.Main.route) {
                MainScreen(
                    viewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(route = Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                val showThemeDialog by settingsViewModel.showThemeDialog.collectAsState()
                val currentTheme by settingsViewModel.currentTheme.collectAsState()

                SettingsScreen(
                    onLanguageClick = { navController.navigate(Screen.LanguageSettings.route) },
                    onBubbleSettingsClick = { navController.navigate(Screen.BubbleSettings.route) },
                    onThemeClick = { settingsViewModel.onThemeSettingClick() },
                    onNotificationClick = { navController.navigate(Screen.NotificationSettings.route) },
                    onAboutClick = { navController.navigate(Screen.About.route) },
                    onBackPressed = { navController.popBackStack() }
                )

                val sheetState = rememberModalBottomSheetState()
                if (showThemeDialog) {
                    ThemeSelectionBottomSheet(
                        currentTheme = currentTheme,
                        onThemeSelected = { themeMode ->
                            settingsViewModel.setTheme(themeMode)
                            settingsViewModel.onDismissThemeDialog()
                        },
                        onDismissRequest = { settingsViewModel.onDismissThemeDialog() },
                        sheetState = sheetState
                    )
                }
            }

            composable(route = Screen.LanguageSettings.route) {
                val languageViewModel: LanguageSettingsViewModel = hiltViewModel()
                val currentSettings by languageViewModel.currentSettings.collectAsState()
                val dialogType by languageViewModel.dialogType.collectAsState()

                LanguageSettingsScreen(
                    currentSettings = currentSettings,
                    onAppLanguageClick = { languageViewModel.showDialog(LanguageDialogType.APP) },
                    onTargetLanguageClick = { languageViewModel.showDialog(LanguageDialogType.TARGET) },
                    onSourceLanguageClick = { languageViewModel.showDialog(LanguageDialogType.SOURCE) },
                    onBackPressed = { navController.popBackStack() }
                )

                dialogType?.let { type ->
                    val (title, currentLanguage, onSelected) = when (type) {
                        LanguageDialogType.APP -> Triple(
                            stringResource(R.string.ui_language),
                            currentSettings.appLanguage
                        ) { lang: Language -> languageViewModel.saveAppLanguage(lang) }

                        LanguageDialogType.TARGET -> Triple(
                            stringResource(R.string.target_language),
                            currentSettings.targetLanguage
                        ) { lang: Language -> languageViewModel.saveTargetLanguage(lang) }

                        LanguageDialogType.SOURCE -> Triple(
                            stringResource(R.string.source_language),
                            currentSettings.sourceLanguage
                        ) { lang: Language -> languageViewModel.saveSourceLanguage(lang) }
                    }

                    @OptIn(ExperimentalMaterial3Api::class)
                    val sheetState = rememberModalBottomSheetState()
                    LanguageSelectionBottomSheet(
                        title = title,
                        availableLanguages = languageViewModel.availableLanguages,
                        currentLanguage = currentLanguage,
                        onLanguageSelected = { language ->
                            onSelected(language)
                            languageViewModel.dismissDialog()
                        },
                        onDismissRequest = { languageViewModel.dismissDialog() },
                        sheetState = sheetState,
                    )
                }
            }

            composable(route = Screen.BubbleSettings.route) {
                val bubbleViewModel: BubbleSettingsViewModel = hiltViewModel()
                val uiState by bubbleViewModel.uiState.collectAsState()

                BubbleSettingsScreen(
                    uiState = uiState,
                    onBubbleEnabledChange = bubbleViewModel::onBubbleEnabledChange,
                    onBubbleSizeChange = bubbleViewModel::onBubbleSizeChange,
                    onTransparencyChange = bubbleViewModel::onTransparencyChange,
                    onVibrationEnabledChange = bubbleViewModel::onVibrationEnabledChange,
                    onAutoHideClick = { navController.navigate(Screen.AutoHideSettings.route) },
                    onAboutBubbleClick = { navController.navigate(Screen.AboutBubble.route) },
                    onBackPressed = { navController.popBackStack() }
                )
            }

            composable(route = Screen.Repetition.route) {
                val repetitionViewModel: RepetitionViewModel = hiltViewModel()
                val uiState by repetitionViewModel.uiState.collectAsState()

                RepetitionScreen(
                    uiState = uiState,
                    onEvent = repetitionViewModel::onEvent,
                    onBackPressed = { navController.popBackStack() }
                )
            }

            composable(route = Screen.AddWord.route) {
                val navigationViewModel: AddWordViewModelProvider = hiltViewModel()
                AddWordScreen (
                    viewModel = navigationViewModel.addWordViewModel,
                    initialText = null,
                    onFinish = { navController.popBackStack() }
                )
            }
            composable(route = Screen.Practice.route) {
                PracticeScreen()
            }
            composable(route = Screen.AllWords.route) {
                AllWordsScreen(
                    onBackPressed = { navController.popBackStack() }
                )
            }
            composable(route = Screen.NotificationSettings.route) {
                val notificationSettingsViewModel: NotificationSettingsViewModel = hiltViewModel()
                NotificationSettingsScreen(
                    onEchoClick = { notificationSettingsViewModel.callEcho() },
                    onBackPressed = { navController.popBackStack()},
                    onGentleClick = { notificationSettingsViewModel.callGentle()},
                    onStreakClick = { notificationSettingsViewModel.callStreak()},
                    onSuccessClick = { notificationSettingsViewModel.callSuccess()}
                )
            }
            composable(route = Screen.About.route) { PlaceholderScreen(stringResource(R.string.placeholder_about)) }
            composable(route = Screen.AboutBubble.route) {
                AboutButtonScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(route = Screen.AutoHideSettings.route) {
                FeatureInDevelopScreen(
                    onBackPressed = { navController.popBackStack() },
                    title = stringResource(R.string.autohide_title),
                    subtitle = stringResource(R.string.autohide_subtitle),
                    featureDescription = stringResource(R.string.feature_autohide_description)
                ) }
            composable(route = Screen.Onboarding.route) {
                val initialSetupViewModel: InitialSetupViewModel = hiltViewModel()
                InitialSetupScreen(
                    viewModel = initialSetupViewModel,
                    onComplete = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.placeholder_for, title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }
}
