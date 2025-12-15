package com.shastkiv.vocab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.shastkiv.vocab.ui.initialsetup.compose.components.AnimatedAppNameRow
import com.shastkiv.vocab.ui.theme.appGradientColors

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.appGradientColors.color1,
                        MaterialTheme.appGradientColors.color2,
                        MaterialTheme.appGradientColors.color3
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors =
                            listOf(
                                MaterialTheme.appGradientColors.color4,
                                Color.Transparent
                            ),
                        radius = 800f
                    )
                )
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedAppNameRow()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SplashScreen()
}