package com.shastkiv.vocab.service

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.shastkiv.vocab.ui.addwordfloating.AddWordFloatingViewModel
import com.shastkiv.vocab.ui.addwordfloating.compose.AddWordDialog
import com.shastkiv.vocab.ui.addwordfloating.compose.state.AddWordUiState
import com.shastkiv.vocab.ui.lifecycle.OverlayLifecycleOwner
import androidx.compose.runtime.CompositionLocalProvider
import com.shastkiv.vocab.ui.theme.VocabAppCoreTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

class DialogViewManager(
    private val context: Context,
    private val overlayLifecycleOwner: OverlayLifecycleOwner,
    private val viewModel: AddWordFloatingViewModel
) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var dialogView: View? = null
    var isShowing = false

    private val dialogParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.CENTER
    }

    fun show(initialText: String? = null) {
        if (isShowing) return

        try {
            overlayLifecycleOwner.create()
            overlayLifecycleOwner.start()
            overlayLifecycleOwner.resume()

            viewModel.initialize(initialText)

            dialogView = createDialogView()
            windowManager.addView(dialogView, dialogParams)
            isShowing = true

        } catch (e: Exception) {
            android.util.Log.e("DialogViewManager", "Failed to show dialog", e)
        }
    }

    fun hide() {
        if (!isShowing) return

        try {
            dialogView?.let { view ->
                if (view.isAttachedToWindow) {
                    windowManager.removeView(view)
                    viewModel.resetState()
                }
            }
            dialogView = null
            isShowing = false
        } catch (e: Exception) {
            android.util.Log.e("DialogViewManager", "Failed to hide dialog", e)
        }
    }

    private fun createDialogView(): View {
        return ComposeView(context).apply {
            setViewTreeLifecycleOwner(overlayLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)

            setContent {
                CompositionLocalProvider(
                    LocalViewModelStoreOwner provides overlayLifecycleOwner
                ) {
                    DialogContent()
                }
            }
        }
    }

    @Composable
    private fun DialogContent() {
        val currentTheme by viewModel.themeMode.collectAsState()
        val uiState by viewModel.uiState.collectAsState()
        val inputWord by viewModel.inputWord.collectAsState()

        var isVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            isVisible = true
        }

        if (!isVisible) {
            LaunchedEffect(Unit) {
                delay(ANIMATION_DURATION_MS.toLong())
                hide()
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)) +
                    scaleIn(
                        animationSpec = tween(ANIMATION_DURATION_MS),
                        initialScale = 0.8f
                    ),
            exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MS)) +
                    scaleOut(
                        animationSpec = tween(ANIMATION_DURATION_MS),
                        targetScale = 0.8f
                    )
        ) {
            VocabAppCoreTheme(themeMode = currentTheme) {
                AddWordDialog(
                    uiState = uiState,
                    inputWord = inputWord,
                    onInputChange = viewModel::onInputChange,
                    onCheckWord = viewModel::onCheckWord,
                    onAddToVocabulary = viewModel::onAddWord,
                    onGetFullInfo = viewModel::onGetFullInfoClicked,
                    onTextToSpeech = viewModel::onTextToSpeech,
                    onMainInfoToggle = viewModel::onMainInfoToggle,
                    onExamplesToggle = viewModel::onExamplesToggle,
                    ontUsageInfoToggle = viewModel::ontUsageInfoToggle,
                    onPaywallDismissed = viewModel::onPaywallDismissed,
                    onSubscribe = {
                        isVisible = false
                    },
                    onDismissRequest = { isVisible = false })
            }
        }

        VocabAppCoreTheme(themeMode = currentTheme) {
            LaunchedEffect(uiState) {
                when (uiState) {
                    is AddWordUiState.Error -> {
                        viewModel.onErrorShown()
                    }
                    else -> {}
                }
            }
        }
    }
    companion object {
        private const val ANIMATION_DURATION_MS = 300
    }
}