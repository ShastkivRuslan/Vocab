package dev.shastkiv.vocab.ui.addword.overlay

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dev.shastkiv.vocab.service.bubble.lifecycle.OverlayLifecycleOwner
import dev.shastkiv.vocab.ui.addword.compose.state.AddWordUiState
import dev.shastkiv.vocab.ui.addword.overlay.compose.AddWordDialog
import dev.shastkiv.vocab.ui.addword.shared.AddWordViewModel
import dev.shastkiv.vocab.ui.permission.MicrophonePermissionActivity
import dev.shastkiv.vocab.ui.theme.VocabAppCoreTheme
import kotlinx.coroutines.delay

class DialogViewManager(
    private val context: Context,
    private val overlayLifecycleOwner: OverlayLifecycleOwner,
    private val viewModel: AddWordViewModel
) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var dialogView: View? = null
    var isShowing = false

    private var originX: Int = 0
    private var originY: Int = 0
    private var originSize: Int = 0

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

    fun show(
        initialText: String? = null,
        bubbleX: Int = 0,
        bubbleY: Int = 0,
        bubbleSize: Int = 0
    ) {
        if (isShowing) return

        try {
            originX = bubbleX
            originY = bubbleY
            originSize = bubbleSize

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

    private fun requestMicPermissionOrSpeak() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            viewModel.onSpeechToText()
        } else {
            val intent = Intent(context, MicrophonePermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
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
        val isListening by viewModel.isListening.collectAsState()
        val isReady by viewModel.isReady.collectAsState()
        val rmsLevel by viewModel.rmsLevel.collectAsState()

        var isVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) { isVisible = true }

        LaunchedEffect(uiState) {
            if (uiState is AddWordUiState.DialogShouldClose) isVisible = false
        }

        if (!isVisible) {
            LaunchedEffect(isVisible) {
                delay(ANIMATION_DURATION_MS.toLong())
                hide()
            }
        }

        val displayMetrics = LocalContext.current.resources.displayMetrics
        val screenW = displayMetrics.widthPixels.toFloat()
        val screenH = displayMetrics.heightPixels.toFloat()

        val pivotX = if (screenW > 0f && originX > 0) (originX / screenW).coerceIn(0f, 1f) else 0.5f
        val pivotY = if (screenH > 0f && originY > 0) (originY / screenH).coerceIn(0f, 1f) else 0.5f

        val transformOrigin = remember(pivotX, pivotY) { TransformOrigin(pivotX, pivotY) }

        VocabAppCoreTheme(themeMode = currentTheme) {
            Box(modifier = Modifier.fillMaxSize()) {

                // Скрим — тільки плавний fade, незалежний від діалогу
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(ANIMATION_SCRIM_MS, easing = FastOutSlowInEasing)),
                    exit = fadeOut(tween(ANIMATION_SCRIM_MS, easing = FastOutSlowInEasing))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.275f))
                    )
                }

                // Діалог — spring scale + fade
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(ANIMATION_FADE_MS, easing = FastOutSlowInEasing)) +
                            scaleIn(
                                tween(ANIMATION_FADE_MS, easing = FastOutSlowInEasing),
                                initialScale = ANIMATION_INITIAL_SCALE,
                                transformOrigin = transformOrigin
                            ),
                    exit = fadeOut(tween(ANIMATION_EXIT_MS, easing = FastOutSlowInEasing)) +
                            scaleOut(
                                tween(ANIMATION_EXIT_MS, easing = FastOutSlowInEasing),
                                targetScale = ANIMATION_EXIT_SCALE,
                                transformOrigin = transformOrigin
                            )
                ) {
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
                        onSubscribe = { isVisible = false },
                        onDismissRequest = { isVisible = false },
                        onRetryManual = viewModel::onRetryToIdle,
                        isListening = isListening,
                        isReady = isReady,
                        rmsLevel = rmsLevel,
                        onSpeechToText = ::requestMicPermissionOrSpeak
                    )
                }
            }
        }
    }

    companion object {
        private const val ANIMATION_SCRIM_MS = 400
        private const val ANIMATION_FADE_MS = 280
        private const val ANIMATION_EXIT_MS = 200
        private const val ANIMATION_DURATION_MS = 280
        private const val ANIMATION_INITIAL_SCALE = 0.88f
        private const val ANIMATION_EXIT_SCALE = 0.92f
    }
}
