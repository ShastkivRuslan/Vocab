package dev.shastkiv.vocab.ui.common.compose

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
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.theme.customColors

@Composable
fun FeatureInDevelopScreen(
    onBackPressed: () -> Unit,
    title: String,
    subtitle: String,
    featureDescription: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onBackPressed() }
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth(),
                text = title,
                fontSize = 32.sp,
                color = MaterialTheme.customColors.cardTitleText
            )
        }

        Text(
            modifier = Modifier.padding(start = 56.dp, top = 4.dp),
            text = subtitle,
            color = MaterialTheme.customColors.cardDescriptionText,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        LiquidGlassCard {
            Text(
                text = featureDescription,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.customColors.cardTitleText,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        LiquidGlassCard {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.feature_in_development_message),
                    color = MaterialTheme.customColors.cardTitleText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    FeatureInDevelopScreen(
        onBackPressed = {},
        title = "Auto-hide Bubble",
        subtitle = "Smart visibility control",
        featureDescription = "Automatically hides the bubble when you open specific applications like games or video players to ensure an uninterrupted experience."
    )
}