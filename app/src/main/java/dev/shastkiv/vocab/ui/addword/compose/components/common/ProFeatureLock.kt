package dev.shastkiv.vocab.ui.addword.compose.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun ProFeatureLock(modifier: Modifier = Modifier) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.mediumCornerRadius))
            .background(colors.expandableCardBackground),
        contentAlignment = Alignment.Center,
    ) {
        ProBadge()
    }
}