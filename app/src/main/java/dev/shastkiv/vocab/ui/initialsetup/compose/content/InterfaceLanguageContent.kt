package dev.shastkiv.vocab.ui.initialsetup.compose.content

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.AvailableLanguages
import dev.shastkiv.vocab.ui.initialsetup.InitialSetupViewModel
import dev.shastkiv.vocab.ui.initialsetup.compose.components.AnimatedAppNameRow
import dev.shastkiv.vocab.ui.initialsetup.compose.components.AnimatedLanguagePrompt
import dev.shastkiv.vocab.ui.initialsetup.compose.components.LanguageCard
import dev.shastkiv.vocab.ui.theme.dimensions

@Composable
fun InterfaceLanguageContent(
    viewModel: InitialSetupViewModel,
    isLoading: Boolean,
    onContinue: () -> Unit
) {
    val currentLanguage by viewModel.currentAppLanguage.collectAsState()
    val availableLanguages = remember { AvailableLanguages.list }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val dimensions = MaterialTheme.dimensions
    val color = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.mediumPadding)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

            AnimatedAppNameRow()

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            AnimatedLanguagePrompt()

            Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

            Column(
                verticalArrangement = Arrangement.spacedBy(dimensions.cardItemSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                availableLanguages.forEach { language ->
                    LanguageCard(
                        language = language,
                        isSelected = currentLanguage.code == language.code,
                        enabled = !isLoading,
                        onClick = {
                            if (currentLanguage.code != language.code) {
                                viewModel.saveAppLanguage(language)
                                Toast.makeText(
                                    context,
                                    when (language.code) {
                                        "uk" -> "Мову змінено"
                                        "en" -> "Language changed"
                                        else -> "Language changed"
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
        }

        Button(
            onClick = onContinue,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.buttonHeight),
            shape = RoundedCornerShape(dimensions.cornerRadius)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimensions.spacingLarge),
                    color = color.onPrimary,
                    strokeWidth = (dimensions.spacingSmall.value / 4).coerceAtLeast(2f).dp
                )
            } else {
                Text(
                    text = stringResource(R.string.continue_button),
                    fontSize = dimensions.buttonTextSize,
                    fontWeight = FontWeight.Bold,
                    color = color.onPrimary
                )
            }
        }
    }
}