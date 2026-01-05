package dev.shastkiv.vocab.ui.settings.language.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.ui.theme.dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionBottomSheet(
    title: String,
    availableLanguages: List<Language>,
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState
) {
    val dimensions = MaterialTheme.dimensions
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = dimensions.spacingExtraLarge)
                .selectableGroup()
        ) {
            Text(
                text = title,
                style = dimensions.promptTextStyle,
                modifier = Modifier.padding(dimensions.mediumPadding)
            )
            HorizontalDivider()

            availableLanguages.forEach { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (language.code == currentLanguage.code),
                            onClick = { onLanguageSelected(language) }
                        )
                        .padding(horizontal = dimensions.mediumPadding, vertical = dimensions.cardItemSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (language.code == currentLanguage.code),
                        onClick = null
                    )
                    Text(
                        text = "${language.flagEmoji} ${language.name}",
                        style = dimensions.subHeaderTextStyle,
                        modifier = Modifier.padding(start = dimensions.mediumPadding)
                    )
                }
            }
        }
    }
}