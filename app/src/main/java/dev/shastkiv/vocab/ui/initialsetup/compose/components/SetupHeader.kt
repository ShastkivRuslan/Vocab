package dev.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun SetupHeader(
    onBackPressed: () -> Unit,
    title: String,
    subTitle: String
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Navigate",
            tint = colors.cardTitleText,
            modifier = Modifier
                .size(dimensions.headerIconSize)
                .clickable { onBackPressed() }
        )

        Spacer(modifier = Modifier.width(dimensions.mediumSpacing))

        Column {
            Text(
                text = title,
                style = typography.header,
                fontWeight = FontWeight.Bold,
                color = colors.cardTitleText
            )

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))

            Text(
                text = subTitle,
                style = typography.subHeader,
                color = colors.cardDescriptionText
            )
        }
    }
}
