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
import androidx.compose.ui.graphics.SolidColor
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
        progressCenterTextSubtitle = Color.Unspecified,
        accent = Color.Unspecified,
        accentSoft = Color.Unspecified,
        onAccent = Color.Unspecified,
        accentContainer = Color.Unspecified,
        errorContainer = Color.Unspecified,
        onErrorContainer = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textMain = Color.Unspecified,
        accentCardGradientStart = Color.Unspecified,
        accentCardGradientToEnd = Color.Unspecified,
        accentCardIconBoxColor = Color.Unspecified,
        expandableCardBorder = SolidColor(Color.Unspecified),
        expandableCardBackground = Color.Unspecified,
        expandableCardArrowTint = Color.Unspecified,
        expandableCardExpandedBackground = SolidColor(Color.Unspecified),
        expandableCardContentBackground = SolidColor(Color.Unspecified),
        textProcessingCardColor = Color.Unspecified,
        textProcessingBackgroundColor = Color.Unspecified,
        overlayDialogColor = Color.Unspecified
    )
}


val MaterialTheme.appGradientColors: GradientBackgroundColors
    @Composable
    get() = LocalAppGradientColors.current

val MaterialTheme.appColors: CustomAppColors
    @Composable
    get() = LocalCustomAppColors.current

val MaterialTheme.appDimensions: AppDimensions
    @Composable
    get() = LocalAppDimensions.current

val MaterialTheme.appTypography: CustomTypography
    @Composable
    get() = LocalAppTypography.current


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

    val gradientColors = if (!isDark) DarkAppGradient else LightAppGradient
    val appColors = if (isDark) DarkCustomColors else LightCustomColors

    val configuration = LocalConfiguration.current

    val appDimensions = when {
        configuration.screenHeightDp < 650 -> smallDimensions
        configuration.screenHeightDp < 900 -> mediumDimensions
        else -> defaultDimensions
    }

    val appTypography = when {
        configuration.screenHeightDp < 650 -> smallTypography
        configuration.screenHeightDp < 900 -> mediumTypography
        else -> defaultTypography
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) {
                val window = context.window
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalAppGradientColors provides gradientColors,
        LocalCustomAppColors provides appColors,
        LocalAppDimensions provides appDimensions,
        LocalAppTypography provides appTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
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
    val isDark = when {
        themeMode != null -> when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> darkTheme
        }
        else -> darkTheme
    }

    LearnWordsTrainerTheme(darkTheme = isDark) {
        content()
    }
}