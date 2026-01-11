package dev.shastkiv.vocab.ui.about.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.common.compose.AboutBubbleContent
import dev.shastkiv.vocab.ui.common.compose.OverlayPermissionAlert
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun AboutButtonScreen(
    onBackClick: () -> Unit
) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Navigate",
                tint = colors.cardTitleText,
                modifier = Modifier
                    .size(dimensions.headerIconSize)
                    .clickable { onBackClick() }
            )
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensions.mediumPadding),
                text = stringResource(R.string.bubble_settings_about),
                style = typography.header,
                color = colors.cardTitleText
            )
        }

        Column(
            modifier = Modifier.padding(dimensions.mediumPadding)
        ) {
            AboutBubbleContent()

            Spacer(modifier = Modifier.height(dimensions.extraLargeSpacing))

            OverlayPermissionAlert()
        }
    }
}

@Composable
@Preview
private fun Preview() {
    AboutButtonScreen(onBackClick = {})
}