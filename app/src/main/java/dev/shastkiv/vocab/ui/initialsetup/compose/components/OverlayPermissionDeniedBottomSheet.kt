package dev.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayPermissionDeniedBottomSheet(
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit,
    sheetState: SheetState
) {
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.mediumPadding)
        ) {
            Text(
                text = stringResource(R.string.overlay_permission_denied_title),
                style = typography.cardTitleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

            Text(
                text = stringResource(R.string.overlay_permission_denied_description),
                style = typography.cardDescriptionMedium,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

            Text(
                text = stringResource(R.string.overlay_permission_why_important_title),
                style = typography.cardTitleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))

            Text(
                text = stringResource(R.string.overlay_permission_why_important_description),
                style = typography.cardDescriptionMedium,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

            Text(
                text = stringResource(R.string.overlay_permission_safety_title),
                style = typography.cardTitleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.accent
            )

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))

            Text(
                text = stringResource(R.string.overlay_permission_safety_description),
                style = dimensions.cartDescriptionMediumStyle,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

            Button(
                onClick = onTryAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.buttonHeight),
                shape = RoundedCornerShape(dimensions.mediumCornerRadius)
            ) {
                Text(
                    text = stringResource(R.string.overlay_permission_try_again),
                    fontSize = typography.buttonTextSize
                )
            }

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.overlay_permission_skip_for_now))
            }

            Spacer(modifier = Modifier.height(dimensions.extraSmallSpacing))
        }
    }
}
