package dev.shastkiv.vocab.ui.initialsetup.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.shastkiv.vocab.domain.model.AvailableLanguages
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.settings.language.compose.LanguageSelectionBottomSheet
import dev.shastkiv.vocab.ui.theme.customColors
import dev.shastkiv.vocab.ui.theme.dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerCard(
    title: String,
    subtitle: String,
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    emoji: String
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val dimensions = MaterialTheme.dimensions
    val typography = MaterialTheme.typography
    val customColors = MaterialTheme.customColors
    val defaultColors = MaterialTheme.colorScheme

    LiquidGlassCard {
        Column(
            modifier = Modifier.padding(dimensions.largePadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
            ) {
                Text(text = emoji, style = typography.titleMedium)
                Text(
                    text = title,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = customColors.cardTitleText
                )
            }

            Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))

            Text(
                text = subtitle,
                style = typography.bodySmall,
                color = defaultColors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            LiquidGlassCard(
                modifier = Modifier.clickable { showBottomSheet = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.mediumPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                    ) {
                        Text(text = selectedLanguage.flagEmoji, style = typography.titleMedium)
                        Text(
                            text = selectedLanguage.name,
                            color = customColors.cardTitleText,
                            style = typography.titleMedium)
                    }
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }
        }
    }
    if (showBottomSheet) {
        LanguageSelectionBottomSheet(
            title = title,
            availableLanguages = AvailableLanguages.list,
            currentLanguage = selectedLanguage,
            onLanguageSelected = { language ->
                onLanguageSelected(language)
                showBottomSheet = false
            },
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState()
        )
    }
}
