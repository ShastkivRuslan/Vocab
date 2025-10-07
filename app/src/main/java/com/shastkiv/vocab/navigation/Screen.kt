package com.shastkiv.vocab.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main_screen")
    object Settings : Screen("settings_screen")
    object Repetition : Screen("repetition_screen")
    object AddWord : Screen("add_word_screen")
    object Practice : Screen("practice_screen")
    object AllWords : Screen("all_words_screen")
    object Onboarding : Screen(route = "onboarding_screen")

    object LanguageSettings : Screen("language_settings_screen")
    object BubbleSettings : Screen("bubble_settings_screen")
    object AutoHideSettings : Screen("auto_hide_settings_screen")
    object NotificationSettings : Screen("notification_settings_screen")
    object About : Screen("about_screen")
    object AboutBubble : Screen("about_bubble_screen")
}