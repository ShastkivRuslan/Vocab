package dev.shastkiv.vocab.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AppDimensions(
    val extraSmallPadding: Dp,
    val smallPadding: Dp,
    val mediumPadding: Dp,
    val largePadding: Dp,
    val cornerRadius: Dp,

    val spacingExtraLarge: Dp,
    val spacingLarge: Dp,
    val spacingMedium: Dp,
    val spacingSmall: Dp,
    val spacingExtraSmall: Dp,

    val buttonHeight: Dp,
    val buttonTextSize: TextUnit,
    val loadingIndicatorSize: Dp,

    val cardItemSpacing: Dp,
    val cardLargeTitleStyle: TextStyle,
    val cardTitleStyle: TextStyle,
    val cartDescriptionStyle: TextStyle,

    val headerIconSize: Dp,
    val appNameHeight: Dp,
    val emojiSize: TextUnit,

    val iconSizeMedium: Dp,
    val iconSizeLarge: Dp,
    val iconSizeExtraLarge: Dp,

    val headerTextStyle: TextStyle,
    val subHeaderTextStyle: TextStyle,
    val promptTextStyle: TextStyle
)

val defaultDimensions = AppDimensions(
    extraSmallPadding = 8.dp,
    smallPadding = 12.dp,
    mediumPadding = 16.dp,
    largePadding = 24.dp,
    cornerRadius = 16.dp,

    spacingExtraLarge = 32.dp,
    spacingLarge = 24.dp,
    spacingMedium = 16.dp,
    spacingSmall = 8.dp,
    spacingExtraSmall = 4.dp,

    buttonHeight = 56.dp,
    buttonTextSize = 18.sp,
    loadingIndicatorSize = 24.dp,

    cardItemSpacing = 12.dp,
    cardLargeTitleStyle = AppTypography.headlineMedium,
    cardTitleStyle = AppTypography.titleMedium,
    cartDescriptionStyle = AppTypography.bodyLarge,

    headerIconSize = 48.dp,
    appNameHeight = 100.dp,
    emojiSize = 24.sp,

    iconSizeMedium = 24.dp,
    iconSizeLarge = 32.dp,
    iconSizeExtraLarge = 64.dp,

    headerTextStyle = AppTypography.headlineLarge,
    subHeaderTextStyle = AppTypography.bodyLarge,
    promptTextStyle = AppTypography.headlineSmall
)

val mediumDimensions = AppDimensions(
    extraSmallPadding = 6.dp,
    smallPadding = 10.dp,
    mediumPadding = 14.dp,
    largePadding = 20.dp,
    cornerRadius = 14.dp,

    spacingExtraLarge = 28.dp,
    spacingLarge = 20.dp,
    spacingMedium = 14.dp,
    spacingSmall = 6.dp,
    spacingExtraSmall = 4.dp,

    buttonHeight = 52.dp,
    buttonTextSize = 17.sp,
    loadingIndicatorSize = 22.dp,

    cardItemSpacing = 10.dp,
    cardLargeTitleStyle = AppTypography.headlineSmall,
    cardTitleStyle = AppTypography.titleMedium,
    cartDescriptionStyle = AppTypography.bodyLarge,

    headerIconSize = 42.dp,
    appNameHeight = 90.dp,
    emojiSize = 22.sp,

    iconSizeMedium = 22.dp,
    iconSizeLarge = 28.dp,
    iconSizeExtraLarge = 56.dp,

    headerTextStyle = AppTypography.headlineMedium,
    subHeaderTextStyle = AppTypography.bodyLarge,
    promptTextStyle = AppTypography.titleLarge
)

val smallDimensions = AppDimensions(
    extraSmallPadding = 4.dp,
    smallPadding = 8.dp,
    mediumPadding = 12.dp,
    largePadding = 12.dp,
    cornerRadius = 12.dp,

    spacingExtraLarge = 20.dp,
    spacingLarge = 16.dp,
    spacingMedium = 12.dp,
    spacingSmall = 4.dp,
    spacingExtraSmall = 3.dp,

    buttonHeight = 48.dp,
    buttonTextSize = 16.sp,
    loadingIndicatorSize = 20.dp,

    cardItemSpacing = 8.dp,
    cardLargeTitleStyle = AppTypography.titleLarge,
    cardTitleStyle = AppTypography.titleSmall,
    cartDescriptionStyle = AppTypography.bodyMedium,

    headerIconSize = 36.dp,
    appNameHeight = 80.dp,
    emojiSize = 20.sp,

    iconSizeMedium = 18.dp,
    iconSizeLarge = 24.dp,
    iconSizeExtraLarge = 48.dp,

    headerTextStyle = AppTypography.headlineSmall,
    subHeaderTextStyle = AppTypography.bodyMedium,
    promptTextStyle = AppTypography.titleMedium
)

val LocalAppDimensions = staticCompositionLocalOf { defaultDimensions }