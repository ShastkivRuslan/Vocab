package dev.shastkiv.vocab.ui.bubble

data class BubbleSettingsUiState(
    val isBubbleEnabled: Boolean = false,
    val bubbleSize: Float = 50f,
    val bubbleTransparency: Float = 50f,
    val isVibrationEnabled: Boolean = true,
    val autoHideAppList: Set<String> = emptySet(),
    val hasOverlayPermission: Boolean = false,
    val showDeniedSheet: Boolean = false
)