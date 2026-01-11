package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.LanguageSettings
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun HeaderTitle(languageSettings: LanguageSettings) {
    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.app_name),
                style = typography.header,
                color = colors.textMain
            )
            Spacer(modifier = Modifier.width(dimensions.mediumSpacing))
            Text(
                text = "${languageSettings.sourceLanguage.flagEmoji} > ${languageSettings.targetLanguage.flagEmoji}",
                color = colors.textMain,
                style = typography.headerEmoji
            )
        }
        Spacer(modifier = Modifier.height(dimensions.smallSpacing))
        Text(
            text = stringResource(R.string.description_head_text),
            style = typography.subHeader,
            color = colors.cardDescriptionText
        )
    }
}