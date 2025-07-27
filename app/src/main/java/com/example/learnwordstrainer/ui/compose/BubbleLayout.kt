package com.example.learnwordstrainer.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.R

@Composable
fun BubbleLayout(
    modifier: Modifier = Modifier,
    size: Dp,
    alpha: Float
) {
    Card(
        modifier = modifier
            .size(size)
            .alpha(alpha),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add_new),
                contentDescription = stringResource(id = R.string.quick_add_word),
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

@Preview
@Composable
private fun BubbleLayoutPreview() {
    BubbleLayout(size = 40.dp,
        alpha = 0.7f)
}