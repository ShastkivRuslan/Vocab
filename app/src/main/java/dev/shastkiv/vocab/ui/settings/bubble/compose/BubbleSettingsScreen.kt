package dev.shastkiv.vocab.ui.settings.bubble

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.bubble.BubbleSettingsUiState
import dev.shastkiv.vocab.ui.initialsetup.compose.components.OverlayPermissionDeniedBottomSheet
import dev.shastkiv.vocab.ui.settings.components.BubbleSettingItem
import dev.shastkiv.vocab.ui.settings.components.SettingItemWithSwitch
import dev.shastkiv.vocab.ui.settings.components.SettingsHeader
import dev.shastkiv.vocab.ui.settings.components.SettingsItemDivider
import dev.shastkiv.vocab.ui.settings.components.SettingsSlider
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography
import kotlinx.coroutines.delay

private const val CONTENT_APPEARANCE_DELAY_MS = 1500L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleSettingsScreen(
    viewModel: BubbleSettingsViewModel,
    uiState: BubbleSettingsUiState,
    onBackPressed: () -> Unit,
    onBubbleEnabledChange: (Boolean) -> Unit,
    onBubbleSizeChange: (Float) -> Unit,
    onTransparencyChange: (Float) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    onAutoHideClick: () -> Unit,
    onAboutBubbleClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var isContentVisible by remember { mutableStateOf(uiState.hasOverlayPermission) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val overlayLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.checkPermission()
    }

    LaunchedEffect(uiState.hasOverlayPermission) {
        if (!uiState.hasOverlayPermission) isContentVisible = false
    }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is BubbleEvent.PermissionGrantedSuccess -> {
                    showSuccessSnackbar = true
                    delay(CONTENT_APPEARANCE_DELAY_MS)
                    isContentVisible = true
                    showSuccessSnackbar = false
                }
                is BubbleEvent.OpenOverlaySettings -> {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${context.packageName}".toUri()
                    )
                    overlayLauncher.launch(intent)
                }
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkPermission()
    }
    if (uiState.showDeniedSheet) {
        OverlayPermissionDeniedBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                viewModel.dismissDeniedSheet()
            },
            onTryAgain = {
                viewModel.dismissDeniedSheet()
                viewModel.requestOverlayPermission()
            }
        )
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsHeader(
                onBackPressed = onBackPressed,
                title = stringResource(R.string.bubble_settings_screen_title),
                subTitle = stringResource(R.string.bubble_settings_screen_subtitle)
            )

            Spacer(Modifier.height(dimensions.smallSpacing))

            AnimatedVisibility(
                visible = !isContentVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(horizontal = dimensions.mediumPadding)) {
                    PermissionWarningCard(
                        onClick = { viewModel.requestOverlayPermission() }
                    )
                }
            }

            AnimatedVisibility(
                visible = isContentVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                BubbleSettingsContent(
                    uiState = uiState,
                    onBubbleEnabledChange = onBubbleEnabledChange,
                    onBubbleSizeChange = onBubbleSizeChange,
                    onTransparencyChange = onTransparencyChange,
                    onVibrationEnabledChange = onVibrationEnabledChange,
                    onAutoHideClick = onAutoHideClick,
                    onAboutBubbleClick = onAboutBubbleClick
                )
            }
        }

        AnimatedVisibility(
            visible = showSuccessSnackbar,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Snackbar(
                modifier = Modifier.padding(dimensions.smallPadding),
                containerColor = colors.accent,
                contentColor = Color.White,
                shape = RoundedCornerShape(dimensions.smallCornerRadius)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(dimensions.smallSpacing))
                    Text(text = stringResource(R.string.magic_activated))
                }
            }
        }
    }
}

@Composable
private fun BubbleSettingsContent(
    uiState: BubbleSettingsUiState,
    onBubbleEnabledChange: (Boolean) -> Unit,
    onBubbleSizeChange: (Float) -> Unit,
    onTransparencyChange: (Float) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    onAutoHideClick: () -> Unit,
    onAboutBubbleClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .padding(horizontal = dimensions.mediumPadding)
            .background(
                color = colors.cardBackground,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = colors.cardBorder,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column(modifier = Modifier.padding(dimensions.smallPadding)) {
            SettingItemWithSwitch(
                title = stringResource(R.string.bubble_enable_title),
                description = stringResource(R.string.bubble_enable_description),
                isChecked = uiState.isBubbleEnabled,
                onCheckedChange = onBubbleEnabledChange
            )

            SettingsItemDivider()

            SettingsSlider(
                title = stringResource(R.string.bubble_size_title),
                description = stringResource(R.string.bubble_size_description),
                currentValue = uiState.bubbleSize,
                valueRange = 30f..80f,
                steps = 9,
                onValueChangeFinished = { onBubbleSizeChange(it) }
            )

            SettingsItemDivider()

            SettingsSlider(
                title = stringResource(R.string.bubble_transparency_title),
                description = stringResource(R.string.bubble_transparency_description),
                currentValue = uiState.bubbleTransparency,
                valueRange = 0f..100f,
                steps = 10,
                onValueChangeFinished = { onTransparencyChange(it) }
            )

            SettingsItemDivider()

            SettingItemWithSwitch(
                title = stringResource(R.string.bubble_vibration_title),
                description = stringResource(R.string.bubble_vibration_description),
                isChecked = uiState.isVibrationEnabled,
                onCheckedChange = onVibrationEnabledChange
            )

            SettingsItemDivider()

            BubbleSettingItem(
                title = stringResource(R.string.autohide_title),
                description = stringResource(R.string.autohide_subtitle),
                onClick = onAutoHideClick
            )

            SettingsItemDivider()

            BubbleSettingItem(
                title = stringResource(R.string.bubble_about_title),
                description = stringResource(R.string.bubble_about_description),
                onClick = onAboutBubbleClick
            )
        }
    }
}

@Composable
fun PermissionWarningCard(onClick: () -> Unit) {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val dimensions = MaterialTheme.appDimensions

    val backgroundColor = colors.accent.copy(alpha = 0.1f)
    val strokeColor = colors.accent.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.largeCornerRadius))
            .background(backgroundColor)
            .border(1.dp, strokeColor, RoundedCornerShape(dimensions.largeCornerRadius))
            .padding(dimensions.mediumPadding)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.vocab_plus_unlock_title),
                style = typography.cardTitleLarge,
                color = colors.textMain,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = stringResource(R.string.vocab_plus_unlock_subtitle),
                style = typography.wordHeadLine,
                color = colors.accent,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.vocab_plus_unlock_description),
                style = typography.cardDescriptionMedium,
                color = colors.textSecondary,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.buttonHeight),
                shape = RoundedCornerShape(dimensions.mediumCornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.vocab_plus_unlock_button),
                    fontWeight = FontWeight.Bold,
                    fontSize = dimensions.buttonTextSize
                )
            }
        }
    }
}