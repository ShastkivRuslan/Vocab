package dev.shastkiv.vocab.ui.flashcard.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.theme.GreenSuccess
import dev.shastkiv.vocab.ui.theme.RedError
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun FlashCardButtons(
    enabled: Boolean,
    onKnow: () -> Unit,
    onDontKnow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensions.mediumSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Не знаю
        Button(
            onClick = onDontKnow,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .height(dimensions.buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedError.copy(alpha = 0.12f),
                contentColor = RedError,
                disabledContainerColor = RedError.copy(alpha = 0.05f),
                disabledContentColor = RedError.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(dimensions.largeCornerRadius),
            border = BorderStroke(
                width = 1.dp,
                color = if (enabled) RedError.copy(alpha = 0.4f) else RedError.copy(alpha = 0.15f)
            ),
            elevation = null
        ) {
            Text(
                text = stringResource(R.string.dont_know),
                style = typography.cardTitleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Знаю
        Button(
            onClick = onKnow,
            enabled = enabled,
            modifier = Modifier
                .weight(1f)
                .height(dimensions.buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenSuccess,
                contentColor = Color.White,
                disabledContainerColor = GreenSuccess.copy(alpha = 0.15f),
                disabledContentColor = GreenSuccess.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(dimensions.largeCornerRadius),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (enabled) 4.dp else 0.dp
            )
        ) {
            Text(
                text = stringResource(R.string.know),
                style = typography.cardTitleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}