package dev.shastkiv.vocab.ui.common.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun OverlayPermissionAlert() {
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    val dimensions = MaterialTheme.appDimensions

    LiquidGlassCard {
        Row(
            modifier = Modifier.padding(dimensions.smallPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.SettingsSuggest,
                contentDescription = null,
                modifier = Modifier.size(dimensions.cardIconSize),
                tint = colors.onErrorContainer
            )
            Spacer(modifier = Modifier.width(dimensions.smallSpacing))
            Text(
                text = stringResource(R.string.overlay_permission_footer_text),
                style = typography.cardDescriptionMedium,
                color = colors.onErrorContainer
            )
        }
    }
}