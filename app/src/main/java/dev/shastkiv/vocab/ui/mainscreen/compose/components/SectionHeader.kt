package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun SectionHeader(title: String) {
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    Row(
        modifier = Modifier.padding(bottom = dimensions.mediumPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = typography.sectionHeader,
            color = colors.textMain
        )
        Spacer(Modifier.width(MaterialTheme.appDimensions.smallSpacing))
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            tint = colors.textMain,
            contentDescription = null
        )
    }
}