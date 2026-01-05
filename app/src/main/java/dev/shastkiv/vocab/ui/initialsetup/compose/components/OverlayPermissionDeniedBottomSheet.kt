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
import dev.shastkiv.vocab.ui.theme.dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayPermissionDeniedBottomSheet(
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit,
    sheetState: SheetState
) {
    val dimensions = MaterialTheme.dimensions

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.largePadding)
        ) {
            Text(
                text = stringResource(R.string.overlay_permission_denied_title),
                style = dimensions.cardLargeTitleStyle,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            Text(
                text = stringResource(R.string.overlay_permission_denied_description),
                style = dimensions.cartDescriptionStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

            Text(
                text = stringResource(R.string.overlay_permission_why_important_title),
                style = dimensions.cardTitleStyle,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(dimensions.spacingSmall))

            Text(
                text = stringResource(R.string.overlay_permission_why_important_description),
                style = dimensions.cartDescriptionStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

            Text(
                text = stringResource(R.string.overlay_permission_safety_title),
                style = dimensions.cartDescriptionStyle,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(dimensions.spacingSmall))

            Text(
                text = stringResource(R.string.overlay_permission_safety_description),
                style = dimensions.cartDescriptionStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

            Button(
                onClick = onTryAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.buttonHeight),
                shape = RoundedCornerShape(dimensions.cornerRadius)
            ) {
                Text(
                    text = stringResource(R.string.overlay_permission_try_again),
                    fontSize = dimensions.buttonTextSize
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spacingSmall))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.overlay_permission_skip_for_now))
            }

            Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))
        }
    }
}
