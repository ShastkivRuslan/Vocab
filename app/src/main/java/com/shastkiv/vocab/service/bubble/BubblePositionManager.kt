package com.shastkiv.vocab.service.bubble

import android.graphics.Point
import android.util.TypedValue
import kotlin.math.pow
import kotlin.math.sqrt

class BubblePositionManager(
    private val screenSize: Point,
    private val displayMetrics: android.util.DisplayMetrics
) {

    fun calculateSnapTargetX(currentX: Int, bubbleWidth: Int): Int {
        return if (currentX < (screenSize.x - bubbleWidth) / 2) {
            MARGIN_HORIZONTAL
        } else {
            screenSize.x - bubbleWidth - MARGIN_HORIZONTAL
        }
    }

    fun isBubbleOverDeleteZone(
        bubbleX: Int,
        bubbleY: Int,
        bubbleWidth: Int,
        bubbleHeight: Int,
        deleteZoneYOffset: Int,
        deleteZoneWidth: Int,
        deleteZoneHeight: Int
    ): Boolean {
        if (deleteZoneWidth == 0) return false

        val bubbleCenterX = bubbleX + (bubbleWidth / 2)
        val bubbleCenterY = bubbleY + (bubbleHeight / 2)
        val deleteZoneCenterX = screenSize.x / 2
        val deleteZoneCenterY = screenSize.y - deleteZoneYOffset - (deleteZoneHeight / 2)

        val distance = sqrt(
            (bubbleCenterX - deleteZoneCenterX).toDouble().pow(2) +
                    (bubbleCenterY - deleteZoneCenterY).toDouble().pow(2)
        )

        val bubbleRadius = bubbleWidth / 2
        val zoneRadius = deleteZoneWidth / 2

        return distance < (bubbleRadius + zoneRadius)
    }

    fun dpToPx(dp: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        displayMetrics
    ).toInt()

    companion object {
        private const val MARGIN_HORIZONTAL = 20
    }
}