package dev.shastkiv.vocab.ui.bubble.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BubbleLayout(
    size: Dp,
    alpha: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDragStart: (Offset) -> Unit = {},
    onDrag: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {}
) {
    // Відстежуємо чи почався drag щоб не тригерити click
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size)
            .alpha(alpha)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            )
            // Drag detector - пріоритет вище ніж tap
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        onDragStart(offset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd()
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragEnd()
                    }
                )
            }
            // Tap detector
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (!isDragging) onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Bubble",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
@Preview
fun BubbleLayoutPreview() {
    BubbleLayout(
        size = 40.dp,
        alpha = 1.0f,
        onClick = {},
        onDrag = {},
        onDragEnd = {},
        onDragStart = {}
    )
}
