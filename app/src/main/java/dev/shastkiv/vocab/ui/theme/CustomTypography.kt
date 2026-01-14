package dev.shastkiv.vocab.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow

data class CustomTypography(
    // === BUTTONS & INPUTS ===
    val buttonTextSize: TextUnit,
    val prompt: TextStyle,
    val wordHeadLine: TextStyle,

    // === CARDS ===
    val cardTitleLarge: TextStyle,
    val cardTitleMedium: TextStyle,
    val cardDescriptionMedium: TextStyle,
    val cardDescriptionSmall: TextStyle,
    val sectionHeader: TextStyle,

    // === PROGRESS & STATS ===
    val progressPercent: TextStyle,
    val progressLabel: TextStyle,
    val emojiSize: TextUnit,

    // === HEADERS ===
    val header: TextStyle,
    val headerEmoji: TextStyle,
    val subHeader: TextStyle,
    val animatedAppName: TextStyle

)

private val base = Typography()

val defaultTypography = CustomTypography(
    buttonTextSize = 18.sp,
    prompt = base.headlineSmall,
    wordHeadLine = base.headlineSmall,
    cardTitleLarge = base.headlineMedium,
    cardTitleMedium = base.titleMedium,
    cardDescriptionMedium = base.bodyLarge,
    cardDescriptionSmall = base.bodySmall,
    sectionHeader = base.titleLarge,
    progressPercent = base.titleLarge,
    progressLabel = base.bodySmall,
    emojiSize = 24.sp,
    header = base.headlineLarge,
    headerEmoji = base.headlineSmall,
    subHeader = base.bodyLarge,
    animatedAppName = base.displayLarge.copy(
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.25f),
            offset = Offset(4f, 4f),
            blurRadius = 8f
        )
    )
)

val mediumTypography = CustomTypography(
    buttonTextSize = 17.sp,
    prompt = base.titleLarge,
    wordHeadLine = base.titleLarge,
    cardTitleLarge = base.headlineSmall,
    cardTitleMedium = base.titleMedium,
    cardDescriptionMedium = base.bodyLarge,
    cardDescriptionSmall = base.labelLarge,
    sectionHeader = base.titleLarge,
    progressPercent = base.titleMedium,
    progressLabel = base.labelSmall,
    emojiSize = 22.sp,
    header = base.headlineMedium,
    headerEmoji = base.titleLarge,
    subHeader = base.bodyLarge,
    animatedAppName = base.displayMedium.copy(
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.2f),
            offset = Offset(3f, 3f),
            blurRadius = 6f
        )
    )
)

val smallTypography = CustomTypography(
    buttonTextSize = 16.sp,
    prompt = base.titleMedium,
    wordHeadLine = base.titleMedium,
    cardTitleLarge = base.titleLarge,
    cardTitleMedium = base.titleSmall,
    cardDescriptionMedium = base.bodyMedium,
    cardDescriptionSmall = base.labelMedium,
    sectionHeader = base.titleMedium,
    progressPercent = base.titleSmall,
    progressLabel = base.labelSmall,
    emojiSize = 20.sp,
    header = base.headlineSmall,
    headerEmoji = base.titleMedium,
    subHeader = base.bodyMedium,
    animatedAppName = base.displaySmall.copy(
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.2f),
            offset = Offset(2f, 2f),
            blurRadius = 4f
        )
    )
)

val LocalAppTypography = staticCompositionLocalOf { defaultTypography }