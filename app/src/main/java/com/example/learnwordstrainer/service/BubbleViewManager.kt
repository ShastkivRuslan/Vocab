package com.example.learnwordstrainer.service

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.BounceInterpolator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.learnwordstrainer.domain.usecase.SaveBubblePositionUseCase
import com.example.learnwordstrainer.domain.model.BubblePosition
import com.example.learnwordstrainer.ui.addwordfloating.AddWordFloatingActivity
import com.example.learnwordstrainer.ui.bubble.compose.BubbleLayout
import com.example.learnwordstrainer.ui.bubble.compose.DeleteZoneLayout
import com.example.learnwordstrainer.ui.lifecycle.OverlayLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class BubbleViewManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val saveBubblePositionUseCase: SaveBubblePositionUseCase,
    private val onBubbleRemovedByUser: () -> Unit,
    initialSize: Dp,
    initialAlpha: Float
) {

    // State
    private var isDragging = false
    private var isDeleteZoneVisible = false

    private var isVibrationEnabled = false
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // Views
    private var bubbleView: View? = null
    private var deleteZoneView: View? = null

    // Layout params
    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var deleteZoneParams: WindowManager.LayoutParams

    // Animation state
    private var bubbleSizeState by mutableStateOf(initialSize)
    private var bubbleAlphaState by mutableFloatStateOf(initialAlpha)
    private var targetDeleteZoneAlpha by mutableFloatStateOf(0f)
    private var targetDeleteZoneScale by mutableFloatStateOf(0f)

    // System services
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val displayMetrics = context.resources.displayMetrics
    private val screenSize = Point()

    // Lifecycle
    private val overlayLifecycleOwner = OverlayLifecycleOwner()

    private val positionManager: BubblePositionManager

    // Animators
    private var snapAnimator: ValueAnimator? = null

    init {
        windowManager.defaultDisplay.getSize(screenSize)
        setupLayoutParams()
        positionManager = BubblePositionManager(screenSize, displayMetrics)
    }

    fun show(initialPosition: BubblePosition) {
        if (bubbleView != null) {
            Log.w(TAG, "Bubble already shown")
            return
        }

        try {
            overlayLifecycleOwner.create()
            overlayLifecycleOwner.start()

            bubbleParams.x = initialPosition.x
            bubbleParams.y = initialPosition.y

            bubbleView = createBubbleView()
            windowManager.addView(bubbleView, bubbleParams)

            Log.d(TAG, "Bubble shown at position (${initialPosition.x}, ${initialPosition.y})")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show bubble", e)
            overlayLifecycleOwner.destroy()
        }
    }

    fun hideViews() {
        bubbleView?.visibility = View.GONE
        hideDeleteZone()
        Log.d(TAG, "Views hidden")
    }

    fun showViews() {
        if (bubbleView != null) {
            bubbleView?.visibility = View.VISIBLE
            Log.d(TAG, "Views shown")
        }
    }

    fun updateBubbleSettings(newSize: Dp, newAlpha: Float, isVibrationEnabled: Boolean) {
        bubbleSizeState = newSize
        bubbleAlphaState = newAlpha
        setVibrationEnabled(isVibrationEnabled)
        Log.d(TAG, "Bubble settings updated: size=${newSize}, alpha=${newAlpha}")
    }


    fun destroy() {
        Log.d(TAG, "Destroying BubbleViewManager")

        overlayLifecycleOwner.destroy()
        cleanupAnimators()
        removeViewsSafely()
    }

    private fun createBubbleView(): View {
        return ComposeView(context).apply {
            setViewTreeLifecycleOwner(overlayLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)

            setContent {
                BubbleLayout(
                    size = bubbleSizeState,
                    alpha = bubbleAlphaState,
                    onClick = ::handleBubbleClick,
                    onDragStart = { offset -> handleDragStart(offset) },
                    onDrag = ::handleDrag,
                    onDragEnd = { handleDragEnd() }
                )
            }
        }
    }

    private fun handleBubbleClick() {
        if (!isDragging) {
            if (isVibrationEnabled) { // <-- Перевіряємо налаштування
                vibrateOnClick()
            }
            openAddWordActivity()
        }
    }

    private fun handleDragStart(offset: androidx.compose.ui.geometry.Offset) {
        isDragging = true
        showDeleteZone()
        Log.d(TAG, "Drag started at offset: $offset")
    }

    private fun handleDrag(dragAmount: androidx.compose.ui.geometry.Offset) {
        updateBubblePosition(dragAmount)
        updateDeleteZoneHighlight()
    }

    private fun handleDragEnd() {
        isDragging = false
        val isOverDeleteZone = isBubbleOverDeleteZone()

        hideDeleteZone()

        if (isOverDeleteZone) {
            handleBubbleRemoval()
        } else {
            snapToEdge()
        }

        Log.d(TAG, "Drag ended")
    }

    private fun openAddWordActivity() {
        try {
            val intent = Intent(context, AddWordFloatingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open AddWordFloatingActivity", e)
        }
    }

    private fun updateBubblePosition(dragAmount: androidx.compose.ui.geometry.Offset) {
        bubbleParams.x += dragAmount.x.roundToInt()
        bubbleParams.y += dragAmount.y.roundToInt()

        try {
            windowManager.updateViewLayout(bubbleView, bubbleParams)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update bubble position", e)
        }
    }

    private fun showDeleteZone() {
        if (isDeleteZoneVisible) return
        isDeleteZoneVisible = true

        if (deleteZoneView == null) {
            deleteZoneView = createDeleteZoneView()
            try {
                windowManager.addView(deleteZoneView, deleteZoneParams)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add delete zone view", e)
                return
            }
        }

        deleteZoneView?.visibility = View.VISIBLE
        animateDeleteZoneIn()
    }

    private fun hideDeleteZone() {
        if (!isDeleteZoneVisible) return
        isDeleteZoneVisible = false

        animateDeleteZoneOut()

        coroutineScope.launch {
            delay(DELETE_ZONE_HIDE_DELAY)
            if (!isDeleteZoneVisible) {
                deleteZoneView?.visibility = View.GONE
            }
        }
    }

    private fun createDeleteZoneView(): View {
        return ComposeView(context).apply {
            setViewTreeLifecycleOwner(overlayLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)

            setContent {
                val animatedAlpha by animateFloatAsState(
                    targetValue = targetDeleteZoneAlpha,
                    label = "alphaAnimation",
                    animationSpec = tween(durationMillis = DELETE_ZONE_ANIMATION_DURATION)
                )

                val animatedScale by animateFloatAsState(
                    targetValue = targetDeleteZoneScale,
                    label = "scaleAnimation",
                    animationSpec = tween(durationMillis = DELETE_ZONE_ANIMATION_DURATION)
                )

                DeleteZoneLayout(
                    alpha = animatedAlpha,
                    scale = animatedScale
                )
            }
        }
    }

    private fun animateDeleteZoneIn() {
        targetDeleteZoneAlpha = DELETE_ZONE_ALPHA_NORMAL
        targetDeleteZoneScale = DELETE_ZONE_SCALE_NORMAL
    }

    private fun animateDeleteZoneOut() {
        targetDeleteZoneAlpha = 0f
        targetDeleteZoneScale = 0f
    }

    private fun updateDeleteZoneHighlight() {
        if (!isDeleteZoneVisible) return

        val isOver = isBubbleOverDeleteZone()
        targetDeleteZoneScale = if (isOver) DELETE_ZONE_SCALE_HIGHLIGHTED else DELETE_ZONE_SCALE_NORMAL
        targetDeleteZoneAlpha = if (isOver) DELETE_ZONE_ALPHA_HIGHLIGHTED else DELETE_ZONE_ALPHA_NORMAL
    }

    private fun snapToEdge() {
        val bubbleWidth = bubbleView?.width ?: dpToPx(BUBBLE_SIZE_DP)
        val targetX = positionManager.calculateSnapTargetX(bubbleParams.x, bubbleWidth)

        snapAnimator?.cancel()
        snapAnimator = ValueAnimator.ofInt(bubbleParams.x, targetX).apply {
            duration = SNAP_ANIMATION_DURATION
            interpolator = BounceInterpolator()

            addUpdateListener { animator ->
                bubbleParams.x = animator.animatedValue as Int
                try {
                    windowManager.updateViewLayout(bubbleView, bubbleParams)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update layout during snap", e)
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    saveBubblePosition()
                }
            })
        }

        snapAnimator?.start()
    }

    private fun saveBubblePosition() {
        coroutineScope.launch {
            try {
                saveBubblePositionUseCase(bubbleParams.x, bubbleParams.y)
                Log.d(TAG, "Bubble position saved: (${bubbleParams.x}, ${bubbleParams.y})")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save bubble position", e)
            }
        }
    }

    private fun handleBubbleRemoval() {
        Log.d(TAG, "Bubble removal requested by user")
        onBubbleRemovedByUser()
    }

    private fun isBubbleOverDeleteZone(): Boolean {
        val currentBubbleView = bubbleView ?: return false
        val currentDeleteZoneView = deleteZoneView ?: return false

        if (!isDeleteZoneVisible) return false

        return positionManager.isBubbleOverDeleteZone(
            bubbleX = bubbleParams.x,
            bubbleY = bubbleParams.y,
            bubbleWidth = currentBubbleView.width,
            bubbleHeight = currentBubbleView.height,
            deleteZoneYOffset = deleteZoneParams.y,
            deleteZoneWidth = currentDeleteZoneView.width,
            deleteZoneHeight = currentDeleteZoneView.height
        )
    }

    private fun setupLayoutParams() {
        bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        deleteZoneParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = DELETE_ZONE_BOTTOM_MARGIN
        }
    }

    private fun cleanupAnimators() {
        snapAnimator?.cancel()
        snapAnimator = null
    }

    fun setVibrationEnabled(isEnabled: Boolean) {
        isVibrationEnabled = isEnabled
    }


    private fun removeViewsSafely() {
        bubbleView?.let { view ->
            if (view.isAttachedToWindow) {
                try {
                    windowManager.removeView(view)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to remove bubble view", e)
                }
            }
        }
        bubbleView = null

        deleteZoneView?.let { view ->
            if (view.isAttachedToWindow) {
                try {
                    windowManager.removeView(view)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to remove delete zone view", e)
                }
            }
        }
        deleteZoneView = null
    }

    private fun vibrateOnClick() {
        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
        vibrator.vibrate(effect)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            displayMetrics
        ).toInt()
    }

    companion object {
        private const val TAG = "BubbleViewManager"

        // Bubble constants
        private const val BUBBLE_SIZE_DP = 40

        // Delete zone constants
        private const val DELETE_ZONE_ALPHA_NORMAL = 0.7f
        private const val DELETE_ZONE_ALPHA_HIGHLIGHTED = 1.0f
        private const val DELETE_ZONE_SCALE_NORMAL = 0.7f
        private const val DELETE_ZONE_SCALE_HIGHLIGHTED = 1.0f
        private const val DELETE_ZONE_BOTTOM_MARGIN = 100
        private const val DELETE_ZONE_ANIMATION_DURATION = 500
        private const val DELETE_ZONE_HIDE_DELAY = 300L

        // Animation constants
        private const val SNAP_ANIMATION_DURATION = 300L
    }
}