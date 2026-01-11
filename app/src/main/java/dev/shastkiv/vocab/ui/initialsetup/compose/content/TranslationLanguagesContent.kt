package dev.shastkiv.vocab.ui.initialsetup.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.AvailableLanguages
import dev.shastkiv.vocab.domain.model.Language
import dev.shastkiv.vocab.ui.components.LiquidGlassCard
import dev.shastkiv.vocab.ui.initialsetup.compose.components.LanguagePickerCard
import dev.shastkiv.vocab.ui.initialsetup.compose.components.SetupHeader
import dev.shastkiv.vocab.ui.theme.appColors
import dev.shastkiv.vocab.ui.theme.appDimensions
import dev.shastkiv.vocab.ui.theme.appTypography

@Composable
fun TranslationLanguagesContent(
    isLoading: Boolean,
    error: String?,
    onLanguagesSelected: (Language, Language) -> Unit,
    onBackPressed: () -> Unit,
    onErrorDismissed: () -> Unit
) {
    var sourceLanguage by remember { mutableStateOf(AvailableLanguages.DEFAULT_ENGLISH) }
    var targetLanguage by remember { mutableStateOf(AvailableLanguages.DEFAULT_UKRAINIAN) }
    var showValidationError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val dimensions = MaterialTheme.appDimensions
    val colors = MaterialTheme.appColors
    val typography = MaterialTheme.appTypography

    LaunchedEffect(sourceLanguage, targetLanguage) {
        showValidationError = sourceLanguage.code == targetLanguage.code
    }

    LaunchedEffect(error) {
        if (error != null) {
            kotlinx.coroutines.delay(3000)
            onErrorDismissed()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensions.mediumPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            SetupHeader(
                onBackPressed = onBackPressed,
                title = stringResource(R.string.initial_setup_translation_languages_title),
                subTitle = stringResource(R.string.initial_setup_translation_languages_sub_title)
            )

            Spacer(modifier = Modifier.height(dimensions.extraLargeSpacing))

            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.mediumSpacing),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.smallSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = colors.onErrorContainer
                        )
                        Text(
                            text = error,
                            style = typography.cardDescriptionMedium,
                            color = colors.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(dimensions.mediumSpacing))
            }

            LanguagePickerCard(
                title = stringResource(R.string.source_language),
                subtitle = stringResource(R.string.source_language_subtitle),
                selectedLanguage = sourceLanguage,
                onLanguageSelected = { sourceLanguage = it },
                emoji = "📚"
            )

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(dimensions.iconSizeMedium)
                )
            }

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))

            LanguagePickerCard(
                title = stringResource(R.string.target_language),
                subtitle = stringResource(R.string.target_language_subtitle),
                selectedLanguage = targetLanguage,
                onLanguageSelected = { targetLanguage = it },
                emoji = "🎯"
            )

            Spacer(modifier = Modifier.weight(1f))

            if (showValidationError) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.errorContainer
                    )
                ) {
                    Text(
                        text = stringResource(R.string.error_same_languages),
                        modifier = Modifier.padding(dimensions.extraSmallPadding),
                        style = typography.cardDescriptionMedium,
                        textAlign = TextAlign.Center,
                        color = colors.onErrorContainer
                    )
                }
            } else {
                LiquidGlassCard(){
                    Text(
                        text = buildAnnotatedString {
                            append(getGreeting(sourceLanguage.code))
                            append(sourceLanguage.flagEmoji)
                            append(" → ")
                            append(getGreeting(targetLanguage.code))
                            append(targetLanguage.flagEmoji)
                        },
                        modifier = Modifier.padding(dimensions.largePadding),
                        style = typography.cardTitleMedium,
                        textAlign = TextAlign.Center,
                        color = colors.cardTitleText
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
        }
        Button(
            onClick = { onLanguagesSelected(sourceLanguage, targetLanguage) },
            enabled = !isLoading && !showValidationError,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.buttonHeight),
            shape = RoundedCornerShape(dimensions.mediumCornerRadius)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(dimensions.loadingIndicatorSize),
                    color = colors.onAccent,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.continue_button),
                    fontSize = typography.buttonTextSize
                )
            }
        }
    }
}

private fun getGreeting(languageCode: String): String {
    return when (languageCode) {
        "uk" -> "Привіт "
        "en" -> "Hello "
        "pl" -> "Cześć "
        "de" -> "Hallo "
        "fr" -> "Bonjour "
        "cs" -> "Ahoj "
        else -> "Hello "
    }
}