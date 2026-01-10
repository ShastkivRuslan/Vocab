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
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

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
    val dimensions = MaterialTheme.appDimensions
    val typography = MaterialTheme.appTypography
    val colors = MaterialTheme.appColors

    LiquidGlassCard {
        Column(
            modifier = Modifier.padding(dimensions.largePadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
            ) {
                Text(text = emoji, style = typography.cardTitleMedium)
                Text(
                    text = title,
                    style = typography.cardTitleMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.cardTitleText
                )
            }

            Spacer(modifier = Modifier.height(dimensions.extraSmallSpacing))

            Text(
                text = subtitle,
                style = typography.cardDescriptionSmall,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(dimensions.mediumSpacing))

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
                        horizontalArrangement = Arrangement.spacedBy(dimensions.smallSpacing)
                    ) {
                        Text(
                            text = selectedLanguage.flagEmoji,
                            style = typography.cardTitleMedium
                        )
                        Text(
                            text = selectedLanguage.name,
                            color = colors.cardTitleText,
                            style = typography.cardTitleMedium)
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
