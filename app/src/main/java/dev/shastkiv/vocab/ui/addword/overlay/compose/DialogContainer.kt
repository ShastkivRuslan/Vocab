package dev.shastkiv.vocab.ui.addword.overlay.compose

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import kotlin.math.roundToInt

@Composable
fun DialogContainer(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismissRequest
            )
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = offset.x.roundToInt(),
                        y = offset.y.roundToInt()
                    )
                }
                .wrapContentSize()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = dimensions.mediumPadding)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = Paint()
                            val frameworkPaint = paint.asFrameworkPaint()

                            frameworkPaint.color = Color.BLACK
                            frameworkPaint.setShadowLayer(
                                70f,
                                0f,
                                30f,
                                Color.argb(60, 0, 0, 0)
                            )
                            canvas.drawRoundRect(
                                0f, 0f, size.width, size.height,
                                dimensions.largeCornerRadius.toPx(),
                                dimensions.largeCornerRadius.toPx(),
                                paint
                            )

                            frameworkPaint.setShadowLayer(
                                40f,
                                0f,
                                15f,
                                Color.argb(120, 0, 0, 0)
                            )
                            canvas.drawRoundRect(
                                0f, 0f, size.width, size.height,
                                dimensions.largeCornerRadius.toPx(),
                                dimensions.largeCornerRadius.toPx(),
                                paint
                            )
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(dimensions.largeCornerRadius))
                        .background(colors.overlayDialogColor)
                        .border(
                            width = 1.dp,
                            brush = colors.expandableCardBorder,
                            shape = RoundedCornerShape(dimensions.largeCornerRadius)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = false
                        ) { }
                ) {
                    Column {
                        content()


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensions.dragBoxHeight)
                                .pointerInput(Unit) {
                                    detectDragGestures { _, dragAmount ->
                                        offset += dragAmount
                                    }
                                }
                                .clickable(enabled = false) {},
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(dimensions.dragLineWidth)
                                    .height(dimensions.dragLineHeight)
                                    .background(
                                        color = colors.cardTitleText.copy(alpha = 0.80f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}