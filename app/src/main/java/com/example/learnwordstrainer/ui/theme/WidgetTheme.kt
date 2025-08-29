package com.example.learnwordstrainer.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders

// 1. Створюємо світлу палітру Material 3 з ваших кольорів
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    error = LightError,
    onPrimary = White,
    onSecondary = White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onError = White
)

// 2. Створюємо темну палітру Material 3 з ваших кольорів
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    error = DarkError,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onError = White
)

// 3. Створюємо об'єкт для Glance, як у гайді
object WidgetGlanceColorScheme {
    val colors = ColorProviders(
        light = LightColorScheme,
        dark = DarkColorScheme
    )
}

// 4. Створюємо саму тему для віджета
@Composable
fun WidgetTheme(
    content: @Composable () -> Unit
) {
    GlanceTheme(
        colors = WidgetGlanceColorScheme.colors,
        content = content
    )
}