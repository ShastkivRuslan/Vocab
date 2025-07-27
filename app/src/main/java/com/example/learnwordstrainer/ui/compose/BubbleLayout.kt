package com.example.learnwordstrainer.ui.compose

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BubbleLayout(
    size: Dp,
    alpha: Float,
    onTouchEvent: (action: Int, x: Float, y: Float, rawX: Float, rawY: Float) -> Boolean = { _, _, _, _, _ -> false },
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .alpha(alpha)
            .background(
                color = Color.Blue,
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onTouchEvent(MotionEvent.ACTION_DOWN, offset.x, offset.y, offset.x, offset.y)
                    },
                    onDrag = { change, dragAmount -> // Додано dragAmount параметр
                        onTouchEvent(
                            MotionEvent.ACTION_MOVE,
                            change.position.x,
                            change.position.y,
                            change.position.x,
                            change.position.y
                        )
                    },
                    onDragEnd = {
                        onTouchEvent(MotionEvent.ACTION_UP, 0f, 0f, 0f, 0f)
                    }
                )
            }
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Контент бульбашки
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Bubble",
            tint = Color.White
        )
    }
}

@Preview
@Composable
private fun BubbleLayoutPreview() {
    BubbleLayout(size = 40.dp,
        alpha = 0.7f)
}