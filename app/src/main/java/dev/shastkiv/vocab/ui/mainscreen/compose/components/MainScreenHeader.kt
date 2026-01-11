package dev.shastkiv.vocab.ui.mainscreen.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.domain.model.LanguageSettings
import dev.shastkiv.vocab.ui.theme.appDimensions

@Composable
fun MainScreenHeader(
    onSettingsClick: () -> Unit,
    languageSettings: LanguageSettings
) {
    val dimensions = MaterialTheme.appDimensions
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.mediumPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderTitle(languageSettings = languageSettings)
        SettingsButton(onClick = onSettingsClick)
    }
}