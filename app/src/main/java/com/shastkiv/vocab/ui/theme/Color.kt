package com.shastkiv.vocab.ui.theme

import androidx.compose.ui.graphics.Color

val LightPrimary = Color(0xFF4AA284)
val LightPrimaryVariant = Color(0xFF3700B3)
val LightSecondary = Color(0xFF3a3346)
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFfff8f6)
val LightCardBackground = Color(0xFFF8FAF7)
val LightTextPrimary = Color(0xFF000000)
val LightError = Color(0xFFEC407A)

val DarkPrimary = Color(0xFF14B8A6)
val DarkPrimaryVariant = Color(0xFF009688)
val DarkSecondary = Color(0xFF3F51B5)
val DarkBackground = Color(0xFF121826)
val DarkSurface = Color(0xFF1E2135)
val DarkCardBackground = Color(0xFF252D41)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkError = Color(0xFFEC407A)

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

val GreenSuccess = Color(0xFF4CAF50)
val RedError = Color(0xFFF44336)

data class GradientBackgroundColors(
    val color1: Color,
    val color2: Color,
    val color3: Color,
    val color4: Color
)

val LightAppGradient = GradientBackgroundColors(
    color1 = Color(0xFF1E1B4B),
    color2 = Color(0xFF2E1065),
    color3 = Color(0xFF312E81),
    color4 = Color(0x1A5EEAD4)
)

val DarkAppGradient = GradientBackgroundColors(
    color1 = Color(0xFFE0F2FE),
    color2 = Color(0xFFEDE9FE),
    color3 = Color(0xFFDBEAFE),
    color4 = Color(0x335EEAD4)
)

data class CustomAppColors(
    val cardBackground: Color,
    val cardBorder: Color,
    val cardTitleText: Color,
    val cardDescriptionText: Color,
    val cardIconTintLight: Color,
    val cardArrowBackground: Color,
    val cardArrowTint: Color,
    val statsLabelText: Color,
    val statsValueText: Color,
    val statsGlowStartColor: Color,

    val progressGlowColor: Color,
    val progressBackgroundCircleColor: Color,
    val progressGradientStart: Color,
    val progressGradientEnd: Color,
    val progressCenterTextTitle: Color,
    val progressCenterTextSubtitle: Color,
    val greenSuccess: Color,
    val redError: Color
)

val LightCustomColors = CustomAppColors(
    cardBackground = Color(0x99FFFFFF),
    cardBorder = Color(0x80CBD5E1),
    cardTitleText = Color(0xFF1E293B),
    cardDescriptionText = Color(0xFF475569),
    cardIconTintLight = Color(0xFF14B8A6),
    cardArrowBackground = Color(0x80CBD5E1),
    cardArrowTint = Color(0xFF475569),

    statsLabelText = Color(0xFF475569),
    statsValueText = Color(0xFF1E293B),
    statsGlowStartColor = Color(0x1A5EEAD4),

    progressGlowColor = Color(0x4D5EEAD4),
    progressBackgroundCircleColor = Color(0x33647480),
    progressGradientStart = Color(0xFF14B8A6),
    progressGradientEnd = Color(0xFF0D9488),
    progressCenterTextTitle = Color(0xFF1E293B),
    progressCenterTextSubtitle = Color(0xFF475569),

    greenSuccess = Color(0xFF4CAF50),
    redError = Color(0xFFF44336)
)

val DarkCustomColors: CustomAppColors
    get() = CustomAppColors(
        cardBackground = Color(0x0DFFFFFF),
        cardBorder = Color(0x1AFFFFFF),
        cardTitleText = Color(0xFFDDD6FE),
        cardDescriptionText = Color(0xB3C4B5FD),
        cardIconTintLight = Color(0xFF14B8A6),
        cardArrowBackground = Color(0x0DFFFFFF),
        cardArrowTint = Color(0xFFC4B5FD),

        statsLabelText = Color(0xB3C4B5FD),
        statsValueText = Color(0xFFDDD6FE),
        statsGlowStartColor = Color(0x0D5EEAD4),

        progressGlowColor = Color(0x335EEAD4),
        progressBackgroundCircleColor = Color(0x1AFFFFFF),
        progressGradientStart = Color(0xFF5EEAD4),
        progressGradientEnd = Color(0xFF2DD4BF),
        progressCenterTextTitle = Color(0xFFDDD6FE),
        progressCenterTextSubtitle = Color(0xB3C4B5FD),


        greenSuccess = Color(0xFF4CAF50),
        redError = Color(0xFFF44336)
    )