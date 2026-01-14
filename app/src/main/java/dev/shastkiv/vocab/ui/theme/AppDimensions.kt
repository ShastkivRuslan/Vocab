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

    val extraSmallCornerRadius: Dp,
    val smallCornerRadius: Dp,
    val mediumCornerRadius: Dp,
    val largeCornerRadius: Dp,

    val extraLargeSpacing: Dp,
    val largeSpacing: Dp,
    val mediumSpacing: Dp,
    val smallSpacing: Dp,
    val extraSmallSpacing: Dp,
    val microSpacing: Dp,

    val buttonHeight: Dp,
    val buttonTextSize: TextUnit,

    val loadingIndicatorSize: Dp,
    val resultFooterAnimationSize: Dp,

    val cardItemSpacing: Dp,
    val cardTitleLargeStyle: TextStyle,
    val cardTitleMediumStyle: TextStyle,
    val cartDescriptionMediumStyle: TextStyle,
    val cardDescriptionSmallStyle: TextStyle,
    val sectionHeaderStyle: TextStyle,
    val cardIconBoxSize: Dp,
    val cardIconSize: Dp,
    val cardArrowBoxSize: Dp,
    val cardArrowIconSize: Dp,

    val headerIconSize: Dp,
    val appNameHeight: Dp,
    val emojiSize: TextUnit,

    val iconSizeMedium: Dp,
    val iconSizeLarge: Dp,
    val iconSizeExtraLarge: Dp,

    val statisticsCircleSize: Dp,
    val statisticBoxBlurSize: Dp,
    val progressCircleRadius: Dp,
    val progressCircleStrokeWidth: Dp,
    val progressPercentStyle: TextStyle,
    val progressLabelStyle:TextStyle,
    val loadingAnimationHeight: Dp,

    val headerTextStyle: TextStyle,
    val subHeaderTextStyle: TextStyle,
    val promptTextStyle: TextStyle,

    val dragBoxHeight: Dp,
    val dragLineHeight: Dp,
    val dragLineWidth: Dp
)

val defaultDimensions = AppDimensions(
    extraSmallPadding = 8.dp,
    smallPadding = 12.dp,
    mediumPadding = 16.dp,
    largePadding = 24.dp,

    extraSmallCornerRadius = 8.dp,
    smallCornerRadius = 12.dp,
    mediumCornerRadius = 16.dp,
    largeCornerRadius = 20.dp,

    extraLargeSpacing = 32.dp,
    largeSpacing = 24.dp,
    mediumSpacing = 16.dp,
    smallSpacing = 8.dp,
    extraSmallSpacing = 4.dp,
    microSpacing = 2.dp,

    buttonHeight = 56.dp,
    buttonTextSize = 18.sp,

    loadingIndicatorSize = 24.dp,
    resultFooterAnimationSize = 100.dp,


    cardItemSpacing = 12.dp,

    cardTitleLargeStyle = Typography.headlineMedium,
    cardTitleMediumStyle = Typography.titleMedium,
    cartDescriptionMediumStyle = Typography.bodyLarge,
    cardDescriptionSmallStyle = Typography.bodySmall,
    sectionHeaderStyle = Typography.titleLarge,

    cardIconBoxSize = 56.dp,
    cardIconSize = 28.dp,
    cardArrowBoxSize = 36.dp,
    cardArrowIconSize = 18.dp,

    headerIconSize = 48.dp,
    appNameHeight = 100.dp,
    emojiSize = 24.sp,

    iconSizeMedium = 24.dp,
    iconSizeLarge = 32.dp,
    iconSizeExtraLarge = 64.dp,

    statisticsCircleSize = 120.dp,
    statisticBoxBlurSize = 2.dp,
    progressCircleRadius = 52.dp,
    progressCircleStrokeWidth = 6.dp,
    progressPercentStyle = Typography.titleLarge ,
    progressLabelStyle = Typography.bodySmall,
    loadingAnimationHeight = 136.dp,

    headerTextStyle = Typography.headlineLarge,
    subHeaderTextStyle = Typography.bodyLarge,
    promptTextStyle = Typography.headlineSmall,

    dragBoxHeight = 32.dp,
    dragLineHeight = 4.dp,
    dragLineWidth = 80.dp
)

val mediumDimensions = AppDimensions(
    extraSmallPadding = 6.dp,
    smallPadding = 10.dp,
    mediumPadding = 14.dp,
    largePadding = 20.dp,

    extraSmallCornerRadius = 6.dp,
    smallCornerRadius = 10.dp,
    mediumCornerRadius = 14.dp,
    largeCornerRadius = 18.dp,


    extraLargeSpacing = 28.dp,
    largeSpacing = 20.dp,
    mediumSpacing = 14.dp,
    smallSpacing = 6.dp,
    extraSmallSpacing = 4.dp,
    microSpacing = 2.dp,

    buttonHeight = 52.dp,
    buttonTextSize = 17.sp,

    loadingIndicatorSize = 22.dp,
    resultFooterAnimationSize = 90.dp,

    cardItemSpacing = 10.dp,
    cardTitleLargeStyle = Typography.headlineSmall,
    cardTitleMediumStyle = Typography.titleMedium,
    cartDescriptionMediumStyle = Typography.bodyLarge,
    cardDescriptionSmallStyle = Typography.labelLarge,
    sectionHeaderStyle = Typography.titleMedium,

    cardIconBoxSize = 48.dp,
    cardIconSize = 24.dp,
    cardArrowBoxSize = 32.dp,
    cardArrowIconSize = 16.dp,

    headerIconSize = 42.dp,
    appNameHeight = 90.dp,
    emojiSize = 22.sp,

    iconSizeMedium = 22.dp,
    iconSizeLarge = 28.dp,
    iconSizeExtraLarge = 56.dp,

    statisticsCircleSize = 100.dp,
    statisticBoxBlurSize = 20.dp,
    progressCircleRadius = 43.dp,
    progressCircleStrokeWidth = 5.dp,
    progressPercentStyle = Typography.titleMedium,
    progressLabelStyle = Typography.labelSmall,
    loadingAnimationHeight = 110.dp,

    headerTextStyle = Typography.headlineMedium,
    subHeaderTextStyle = Typography.bodyLarge,
    promptTextStyle = Typography.titleLarge,

    dragBoxHeight = 28.dp,
    dragLineHeight = 4.dp,
    dragLineWidth = 70.dp
)

val smallDimensions = AppDimensions(
    extraSmallPadding = 4.dp,
    smallPadding = 8.dp,
    mediumPadding = 12.dp,
    largePadding = 12.dp,

    extraSmallCornerRadius = 4.dp,
    smallCornerRadius = 8.dp,
    mediumCornerRadius = 12.dp,
    largeCornerRadius = 16.dp,

    extraLargeSpacing = 20.dp,
    largeSpacing = 16.dp,
    mediumSpacing = 12.dp,
    smallSpacing = 4.dp,
    extraSmallSpacing = 3.dp,
    microSpacing = 1.dp,

    buttonHeight = 48.dp,
    buttonTextSize = 16.sp,

    loadingIndicatorSize = 20.dp,
    resultFooterAnimationSize = 80.dp,

    cardItemSpacing = 8.dp,
    cardTitleLargeStyle = Typography.titleLarge,
    cardTitleMediumStyle = Typography.titleSmall,
    cartDescriptionMediumStyle = Typography.bodyMedium,
    cardDescriptionSmallStyle = Typography.labelMedium,
    sectionHeaderStyle = Typography.titleSmall,

    cardIconBoxSize = 40.dp,
    cardIconSize = 20.dp,
    cardArrowBoxSize = 28.dp,
    cardArrowIconSize = 14.dp,

    headerIconSize = 36.dp,
    appNameHeight = 80.dp,
    emojiSize = 20.sp,

    iconSizeMedium = 18.dp,
    iconSizeLarge = 24.dp,
    iconSizeExtraLarge = 48.dp,

    statisticsCircleSize = 80.dp,
    statisticBoxBlurSize = 12.dp,
    progressCircleRadius = 34.dp,
    progressCircleStrokeWidth = 4.dp,
    progressPercentStyle = Typography.titleSmall,
    progressLabelStyle = Typography.labelSmall,
    loadingAnimationHeight = 90.dp,


    headerTextStyle = Typography.headlineSmall,
    subHeaderTextStyle = Typography.bodyMedium,
    promptTextStyle = Typography.titleMedium,

    dragBoxHeight = 24.dp,
    dragLineHeight = 3.dp,
    dragLineWidth = 60.dp
)

val LocalAppDimensions = staticCompositionLocalOf { defaultDimensions }