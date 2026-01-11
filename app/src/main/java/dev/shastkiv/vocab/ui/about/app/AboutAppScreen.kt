package dev.shastkiv.vocab.ui.about.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.initialsetup.compose.components.AnimatedAppNameRow
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun AboutAppScreen(
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
                text = stringResource(R.string.about_us),
                style = typography.header,
                color = colors.cardTitleText
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.mediumPadding)
        ) {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                AnimatedAppNameRow()
            }

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))

            LiquidGlassCard {
                Column(modifier = Modifier.padding(dimensions.mediumPadding)) {
                    Text(
                        text = stringResource(R.string.about_app_mission_title),
                        style = typography.cardTitleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.cardTitleText
                    )

                    Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

                    Text(
                        text = stringResource(R.string.about_app_full_story),
                        style = typography.cardDescriptionMedium,
                        textAlign = TextAlign.Start,
                        color = colors.cardDescriptionText
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.extraLargeSpacing))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensions.mediumPadding),
                text = stringResource(R.string.about_us_footer),
                textAlign = TextAlign.Center,
                style = typography.cardDescriptionSmall,
                color = colors.cardDescriptionText.copy(alpha = 0.5f)
            )
        }
    }
}


@Preview
@Composable
private fun Preview() {
    AboutAppScreen(
        onBackClick = {}
    )
}