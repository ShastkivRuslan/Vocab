package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun LoadingContent() {
    val dimensions = MaterialTheme.appDimensions
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.largePadding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}