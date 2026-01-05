package dev.shastkiv.vocab.ui.theme

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = White,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onSecondary = White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    error = DarkError,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = White,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onSecondary = White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    error = LightError,
    onError = White
)

val LocalAppGradientColors = staticCompositionLocalOf {
    GradientBackgroundColors(
        color1 = Color.Unspecified,
        color2 = Color.Unspecified,
        color3 = Color.Unspecified,
        color4 = Color.Unspecified
    )
}

val LocalCustomAppColors = staticCompositionLocalOf {
    CustomAppColors(
        cardBackground = Color.Unspecified,
        cardBorder = Color.Unspecified,
        cardTitleText = Color.Unspecified,
        cardDescriptionText = Color.Unspecified,
        cardIconTintLight = Color.Unspecified,
        cardArrowBackground = Color.Unspecified,
        cardArrowTint = Color.Unspecified,
        greenSuccess = Color.Unspecified,
        redError = Color.Unspecified,
        statsLabelText = Color.Unspecified,
        statsValueText = Color.Unspecified,
        statsGlowStartColor = Color.Unspecified,
        progressGlowColor = Color.Unspecified,
        progressBackgroundCircleColor = Color.Unspecified,
        progressGradientStart = Color.Unspecified,
        progressGradientEnd = Color.Unspecified,
        progressCenterTextTitle = Color.Unspecified,
        progressCenterTextSubtitle = Color.Unspecified
    )
}


val MaterialTheme.appGradientColors: GradientBackgroundColors
    @Composable
    get() = LocalAppGradientColors.current

val MaterialTheme.customColors: CustomAppColors
    @Composable
    get() = LocalCustomAppColors.current

val MaterialTheme.dimensions: AppDimensions
    @Composable
    get() = LocalAppDimensions.current


@Composable
fun LearnWordsTrainerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeMode: Int? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeMode != null -> when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> DarkColorScheme
            AppCompatDelegate.MODE_NIGHT_NO -> LightColorScheme
            else -> if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val isDark = when (themeMode) {
        AppCompatDelegate.MODE_NIGHT_YES -> true
        AppCompatDelegate.MODE_NIGHT_NO -> false
        else -> darkTheme
    }

    val customGradientColors = if (!isDark) DarkAppGradient else LightAppGradient
    val customAppColors = if (isDark) DarkCustomColors else LightCustomColors

    val configuration = LocalConfiguration.current
    val dimensions = when {
        configuration.screenHeightDp < 720 -> smallDimensions
        configuration.screenHeightDp < 820 -> mediumDimensions
        else -> defaultDimensions
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalAppGradientColors provides customGradientColors,
                LocalCustomAppColors provides customAppColors,
        LocalAppDimensions provides dimensions
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Composable
fun VocabAppCoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeMode: Int? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeMode != null -> when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> DarkColorScheme
            AppCompatDelegate.MODE_NIGHT_NO -> LightColorScheme
            else -> if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}