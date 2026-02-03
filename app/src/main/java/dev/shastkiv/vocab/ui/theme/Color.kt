package dev.shastkiv.vocab.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val LightPrimary = Color(0xFF4AA284)
val LightSecondary = Color(0xFF3a3346)
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFfff8f6)
val LightTextPrimary = Color(0xFF000000)
val LightError = Color(0xFFEC407A)

val DarkPrimary = Color(0xFF14B8A6)
val DarkSecondary = Color(0xFF3F51B5)
val DarkBackground = Color(0xFF121826)
val DarkSurface = Color(0xFF1E2135)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkError = Color(0xFFDA3F74)

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
    val promoCardBackgroundGradient: Brush,
    val accentCardIconBoxColor: Color,
    val greenSuccess: Color,
    val redError: Color,

    val accent: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val textSecondary: Color,
    val textMain: Color,

    val expandableCardBackground: Color,
    val expandableCardBorder: Brush,
    val expandableCardExpandedBackground: Brush,
    val expandableCardContentBackground: Brush,
    val expandableCardArrowTint: Color,

    val textProcessingBackgroundColor: Color,
    val textProcessingCardColor: Color,

    val overlayDialogColor: Color,

    val masteryProgressHighlight: Color,
    val masteryProgressUltraBright: Color,
    val masteryProgressDecreaseWave: Color,
    val masteryProgressIncreaseWave: Color,

    val vocabProgressIdle: Color,
    val vocabProgressActive: Color,
    val vocabProgressCorrect: Color,
    val vocabProgressWrong: Color
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
    promoCardBackgroundGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF32dff5).copy(alpha = 0.3f),
            Color(0xFF84f0e0).copy(alpha = 0.3f)
        )
    ),
    accentCardIconBoxColor = Color.White.copy(alpha = 0.4f),
    greenSuccess = Color(0xFF4CAF50),
    redError = Color(0xFFF44336),

    accent = LightPrimary,
    accentSoft = LightPrimary.copy(alpha = 0.1f),
    onAccent = White,
    accentContainer = LightPrimary,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    textSecondary = Color(0xFF475569),
    textMain = Black,

    expandableCardBackground = LightPrimary.copy(alpha = 0.05f),
    expandableCardBorder = Brush.verticalGradient(
        listOf(
            Color.White,
            LightPrimary.copy(alpha = 0.2f)
        )
    ),
    expandableCardExpandedBackground = Brush.horizontalGradient(
        listOf(
            LightPrimary.copy(alpha = 0.15f),
            LightPrimary.copy(alpha = 0.05f)
        )
    ),
    expandableCardContentBackground = Brush.verticalGradient(
        listOf(
            LightPrimary.copy(alpha = 0.08f),
            Color.Transparent
        )
    ),
    expandableCardArrowTint = LightPrimary,

    textProcessingBackgroundColor = Color.Black.copy(alpha = 0.25f),
    textProcessingCardColor = Color.White.copy(0.95f),
    overlayDialogColor = Color(0xFFE7FCF1),

    masteryProgressHighlight = Color(0xFF4DB8A8),
    masteryProgressUltraBright = Color(0xFF7BC4BA),
    masteryProgressDecreaseWave = Color(0xFFE57373),
    masteryProgressIncreaseWave = Color(0xFF4DB8A8),

    vocabProgressIdle = Color(0xFFCBD5E1),
    vocabProgressActive = Color(0xFF94A3B8),
    vocabProgressCorrect = LightPrimary,
    vocabProgressWrong = Color(0xFFE57373)
)

val DarkCustomColors = CustomAppColors(
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
    promoCardBackgroundGradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF6366F1).copy(alpha = 0.3f),
            Color(0xFF8B5CF6).copy(alpha = 0.3f)
        )
    ),
    accentCardIconBoxColor = Color.White.copy(alpha = 0.1f),
    greenSuccess = Color(0xFF4CAF50),
    redError = Color(0xFFF44336),

    accent = DarkPrimary,
    accentSoft = DarkPrimary.copy(alpha = 0.1f),
    onAccent = White,
    accentContainer = DarkPrimary,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    textSecondary = Color(0xB3C4B5FD),
    textMain = White,

    expandableCardBackground = Color(0xFF1E293B).copy(alpha = 0.4f),
    expandableCardBorder = Brush.verticalGradient(
        listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
    ),
    expandableCardExpandedBackground = Brush.horizontalGradient(
        listOf(Color(0xFF818CF8).copy(alpha = 0.2f), Color(0xFFC4B5FD).copy(alpha = 0.1f))
    ),
    expandableCardContentBackground = Brush.verticalGradient(
        listOf(
            Color.Black.copy(alpha = 0.3f),
            Color.Transparent
        )
    ),
    expandableCardArrowTint = Color(0xFFA5B4FC),

    textProcessingCardColor = Color(0xFF303130).copy(0.90f),
    textProcessingBackgroundColor = Color.Black.copy(0.15f),
    overlayDialogColor = Color(0xFF303130),

    masteryProgressHighlight = Color(0xFF00FFC2),
    masteryProgressUltraBright = Color(0xFF7FFFD4),
    masteryProgressDecreaseWave = Color(0xFFFF6B6B),
    masteryProgressIncreaseWave = Color(0xFF00FFC2),

    vocabProgressIdle = Color(0x4DFFFFFF),
    vocabProgressActive = Color(0xB3FFFFFF),
    vocabProgressCorrect = DarkPrimary,
    vocabProgressWrong = Color(0xFFFF6B6B)
)