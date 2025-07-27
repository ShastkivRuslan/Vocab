package com.example.learnwordstrainer.service

import android.animation.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.util.TypedValue
import android.view.*
import android.view.animation.BounceInterpolator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.learnwordstrainer.domain.SaveBubblePositionUseCase
import com.example.learnwordstrainer.model.BubblePosition
import com.example.learnwordstrainer.ui.activities.AddWordFloatingActivity
import com.example.learnwordstrainer.ui.compose.BubbleLayout
import com.example.learnwordstrainer.ui.compose.DeleteZoneLayout
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class BubbleViewManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val saveBubblePositionUseCase: SaveBubblePositionUseCase,
    private val onBubbleRemovedByUser: () -> Unit
) {
    private var isDragging = false
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val displayMetrics = context.resources.displayMetrics
    private val screenSize = Point()

    private var bubbleView: View? = null
    private var deleteZoneView: View? = null

    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var deleteZoneParams: WindowManager.LayoutParams

    // Змінено: Стан для Compose, який буде оновлюватись динамічно
    private var bubbleSizeState by mutableStateOf(40.dp)
    private var bubbleAlphaState by mutableStateOf(1.0f)

    private var isDeleteZoneVisible = false
    private var zoneAnimator: ValueAnimator? = null

    private val savedStateRegistryOwner = object : SavedStateRegistryOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val lifecycle: Lifecycle = lifecycleRegistry
        override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

        init {
            // Спочатку встановлюємо INITIALIZED стан
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
            // Потім викликаємо performRestore
            savedStateRegistryController.performRestore(null)
            // Тепер можемо переходити до CREATED та STARTED
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun destroy() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
    }

    init {
        windowManager.defaultDisplay.getSize(screenSize)
        setupLayoutParams()
    }

    // Новий метод: для оновлення вигляду з BubbleService
    fun updateBubbleStyle(size: Float, transparency: Float) {
        bubbleSizeState = size.dp
        bubbleAlphaState = transparency / 100f // Переводимо 0-100 в 0.0-1.0
    }

    fun show(initialPosition: BubblePosition) {
        if (bubbleView != null) return
        bubbleParams.x = initialPosition.x
        bubbleParams.y = initialPosition.y
        bubbleView = createBubbleView() // Метод тепер використовує Compose
        windowManager.addView(bubbleView, bubbleParams)
    }

    private fun createBubbleView(): View {
        return ComposeView(context).apply {
            setViewTreeLifecycleOwner(savedStateRegistryOwner)
            setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
            setContent {
                BubbleLayout(
                    size = bubbleSizeState,
                    alpha = bubbleAlphaState,
                    onTouchEvent = { action, x, y, rawX, rawY ->
                        // Передаємо touch events в Compose компонент
                        handleBubbleTouch(action, x, y, rawX, rawY)
                    },
                    onClick = {
                        context.startActivity(Intent(context, AddWordFloatingActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    }
                )
            }
            // Прибираємо setOnTouchListener та setOnClickListener звідси
        }
    }

    private fun manageDeleteZone(show: Boolean) {
        if (show) {
            if (isDeleteZoneVisible) return
            isDeleteZoneVisible = true

            if (deleteZoneView == null) {
                // Змінено: Створюємо ComposeView для зони видалення
                deleteZoneView = ComposeView(context).apply {
                    setViewTreeLifecycleOwner(savedStateRegistryOwner)
                    setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner as? SavedStateRegistryOwner)
                    setContent {
                        DeleteZoneLayout(size = 64.dp,
                            alpha = 0.7f)
                    }
                    visibility = View.GONE
                }
                windowManager.addView(deleteZoneView, deleteZoneParams)
            }

            deleteZoneView?.visibility = View.VISIBLE
            startZoneAnimator(0f, 1f)
        } else {
            if (!isDeleteZoneVisible) return
            isDeleteZoneVisible = false
            startZoneAnimator(1f, 0f) {
                deleteZoneView?.visibility = View.GONE
            }
        }
    }

    // ... решта коду BubbleViewManager залишається без змін ...
    // (snapToEdge, updateDeleteZoneHighlight, BubbleTouchListener, destroy, etc.)

    fun hideViews() {
        bubbleView?.visibility = View.GONE
        manageDeleteZone(show = false)
    }

    fun showViews() {
        bubbleView?.visibility = View.VISIBLE
    }

    fun destroy() {
        savedStateRegistryOwner.destroy()
        coroutineScope.cancel()
        bubbleView?.let { if (it.isAttachedToWindow) windowManager.removeView(it) }
        bubbleView = null
        deleteZoneView?.let {
            zoneAnimator?.cancel()
            if (it.isAttachedToWindow) windowManager.removeView(it)
        }
        deleteZoneView = null
    }

    private fun startZoneAnimator(from: Float, to: Float, onEnd: (() -> Unit)? = null) {
        zoneAnimator?.cancel()
        zoneAnimator = ValueAnimator.ofFloat(from, to).apply {
            duration = 220
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                deleteZoneView?.apply {
                    alpha = value
                    scaleX = 0.7f + (value * 0.3f)
                    scaleY = 0.7f + (value * 0.3f)
                }
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
        }
        zoneAnimator?.start()
    }

    private fun updateDeleteZoneHighlight() {
        if (!isDeleteZoneVisible) return
        val isOver = isBubbleOverDeleteZone()
        val scale = if (isOver) 1.2f else 1.0f
        deleteZoneView?.scaleX = scale
        deleteZoneView?.scaleY = scale
    }

    private inner class BubbleTouchListener : View.OnTouchListener {
        private var isDragging = false
        private var initialX: Int = 0
        private var initialY: Int = 0
        private var initialTouchX: Float = 0f
        private var initialTouchY: Float = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = bubbleParams.x
                    initialY = bubbleParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val touchThreshold = 20
                    if (!isDragging && (kotlin.math.abs(event.rawX - initialTouchX) > touchThreshold || kotlin.math.abs(event.rawY - initialTouchY) > touchThreshold)) {
                        isDragging = true
                        manageDeleteZone(show = true)
                    }
                    if (isDragging) {
                        bubbleParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        bubbleParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(bubbleView, bubbleParams)
                        updateDeleteZoneHighlight()
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        if (isBubbleOverDeleteZone()) {
                            onBubbleRemovedByUser()
                        } else {
                            manageDeleteZone(show = false)
                            snapToEdge()
                        }
                    } else {
                        v.performClick()
                    }
                    return true
                }
            }
            return false
        }
    }

    private fun setupLayoutParams() {
        bubbleParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.START }
        deleteZoneParams = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, PixelFormat.TRANSLUCENT).apply { gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL; y = 100 }
    }

    private fun snapToEdge() {
        val bubbleWidth = bubbleView?.width ?: dpToPx(40)
        val targetX = if (bubbleParams.x < (screenSize.x - bubbleWidth) / 2) MARGIN_HORIZONTAL else screenSize.x - bubbleWidth - MARGIN_HORIZONTAL
        ValueAnimator.ofInt(bubbleParams.x, targetX).apply {
            duration = 300
            interpolator = BounceInterpolator()
            addUpdateListener {
                bubbleParams.x = it.animatedValue as Int
                windowManager.updateViewLayout(bubbleView, bubbleParams)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    coroutineScope.launch { saveBubblePositionUseCase(bubbleParams.x, bubbleParams.y) }
                }
            })
        }.start()
    }

    private fun isBubbleOverDeleteZone(): Boolean {
        if (!isDeleteZoneVisible || bubbleView == null || deleteZoneView == null || deleteZoneView!!.width == 0) {
            return false
        }
        val bubbleCenterX = bubbleParams.x + (bubbleView!!.width / 2)
        val bubbleCenterY = bubbleParams.y + (bubbleView!!.height / 2)
        val deleteZoneCenterX = screenSize.x / 2
        val deleteZoneCenterY = screenSize.y - deleteZoneParams.y - (deleteZoneView!!.height / 2)
        val distance = sqrt((bubbleCenterX - deleteZoneCenterX).toDouble().pow(2) + (bubbleCenterY - deleteZoneCenterY).toDouble().pow(2))
        val bubbleRadius = bubbleView!!.width / 2
        val zoneRadius = deleteZoneView!!.width / 2
        return distance < (bubbleRadius + zoneRadius)
    }
    private fun handleBubbleTouch(action: Int, x: Float, y: Float, rawX: Float, rawY: Float): Boolean {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = bubbleParams.x
                initialY = bubbleParams.y
                initialTouchX = rawX
                initialTouchY = rawY
                isDragging = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val touchThreshold = 20
                if (!isDragging && (abs(rawX - initialTouchX) > touchThreshold || abs(rawY - initialTouchY) > touchThreshold)) {
                    isDragging = true
                    manageDeleteZone(show = true)
                }
                if (isDragging) {
                    bubbleParams.x = initialX + (rawX - initialTouchX).toInt()
                    bubbleParams.y = initialY + (rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(bubbleView, bubbleParams)
                    updateDeleteZoneHighlight()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    if (isBubbleOverDeleteZone()) {
                        onBubbleRemovedByUser()
                    } else {
                        manageDeleteZone(show = false)
                        snapToEdge()
                    }
                } else {
                    // Викликаємо onClick з BubbleLayout
                    return false // Дозволяємо Compose обробити клік
                }
                return true
            }
        }
        return false
    }


    private fun dpToPx(dp: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).toInt()
    companion object { private const val MARGIN_HORIZONTAL = 20 }
}