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
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.common.compose.AboutBubbleContent
import dev.shastkiv.vocab.ui.common.compose.OverlayPermissionAlert
import dev.shastkiv.vocab.ui.theme.customColors

@Composable
fun AboutButtonScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Navigate",
                tint = MaterialTheme.customColors.cardTitleText,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onBackClick() }
            )
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                text = stringResource(R.string.bubble_settings_about),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.customColors.cardTitleText
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AboutBubbleContent()

            Spacer(modifier = Modifier.height(24.dp))

            OverlayPermissionAlert()
        }
    }
}

@Composable
@Preview
private fun Preview() {
    AboutButtonScreen(onBackClick = {})
}