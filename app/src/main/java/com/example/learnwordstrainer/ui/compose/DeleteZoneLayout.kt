package com.example.learnwordstrainer.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.R

@Composable
fun DeleteZoneLayout(
    modifier: Modifier = Modifier,
    size: Dp,
    alpha: Float) {
    Card(
        modifier = modifier
            .size(size)
            .alpha(alpha),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE91E63)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.close_bubble),
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
    }
}

@Preview
@Composable
private fun DeleteZoneLayoutPreview() {
    DeleteZoneLayout(size = 40.dp,
        alpha = 0.9f)
}