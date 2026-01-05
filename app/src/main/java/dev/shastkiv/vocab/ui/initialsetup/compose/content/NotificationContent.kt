package dev.shastkiv.vocab.ui.initialsetup.compose.content

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.initialsetup.compose.components.SetupHeader
import dev.shastkiv.vocab.ui.theme.customColors
import dev.shastkiv.vocab.ui.theme.dimensions

@Composable
fun NotificationContent(
    onSkipPressed: () -> Unit,
    onConfirmPressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onConfirmPressed()
        } else {
            onSkipPressed()
        }
    }
    val scrollState = rememberScrollState()
    val dimensions = MaterialTheme.dimensions

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensions.mediumPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            SetupHeader(
                onBackPressed = onBackPressed,
                title = stringResource(R.string.initial_setup_notification_title),
                subTitle = stringResource(R.string.initial_setup_notification_sub_title)
            )
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            LiquidGlassCard {
                Column(
                    modifier = Modifier.padding(dimensions.largePadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔔",
                        style = dimensions.headerTextStyle
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                    Text(
                        text = stringResource(R.string.notification_stay_informed),
                        style = dimensions.cardTitleStyle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.customColors.cardTitleText
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                    Text(
                        text = stringResource(R.string.notification_description),
                        textAlign = TextAlign.Center,
                        style = dimensions.cartDescriptionStyle,
                        color = MaterialTheme.customColors.cardDescriptionText
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

            LiquidGlassCard {
                Column(modifier = Modifier.padding(dimensions.mediumPadding)) {
                    Text(
                        text = stringResource(R.string.notification_benefits_title),
                        style = dimensions.cardTitleStyle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.customColors.cardTitleText
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    Text(
                        text = stringResource(R.string.notification_benefits_list),
                        style = dimensions.cartDescriptionStyle,
                        color = MaterialTheme.customColors.cardDescriptionText
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            LiquidGlassCard {
                Column(modifier = Modifier.padding(dimensions.mediumPadding)) {
                    Text(
                        text = stringResource(R.string.notification_bubble_stability_title),
                        style = dimensions.cardTitleStyle,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.customColors.cardTitleText
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    Text(
                        text = stringResource(R.string.notification_bubble_stability_description),
                        style = dimensions.cartDescriptionStyle,
                        color = MaterialTheme.customColors.cardDescriptionText
                    )
                }
            }
        }

        TextButton(
            onClick = onSkipPressed,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.mediumPadding)
        ) {
            Text(stringResource(R.string.notification_skip_button))
        }

        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onConfirmPressed()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.buttonHeight),
            shape = RoundedCornerShape(dimensions.cornerRadius)
        ) {
            Text(stringResource(R.string.notification_enable_button), fontSize = dimensions.buttonTextSize)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NotificationContent(
        onSkipPressed = {},
        onConfirmPressed = {},
        onBackPressed = {}
    )
}