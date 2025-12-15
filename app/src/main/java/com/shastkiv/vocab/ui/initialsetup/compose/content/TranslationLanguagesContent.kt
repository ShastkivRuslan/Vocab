package com.shastkiv.vocab.ui.initialsetup.compose.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.shastkiv.vocab.R
import com.shastkiv.vocab.domain.model.AvailableLanguages
import com.shastkiv.vocab.domain.model.Language
import com.shastkiv.vocab.ui.initialsetup.compose.components.LanguagePickerCard
import com.shastkiv.vocab.ui.initialsetup.compose.components.SetupHeader

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

    LaunchedEffect(sourceLanguage, targetLanguage) {
        showValidationError = sourceLanguage.code == targetLanguage.code
    }

    LaunchedEffect(error) {
        if (error != null) {
            kotlinx.coroutines.delay(3000)
            onErrorDismissed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SetupHeader(
            onBackPressed = onBackPressed,
            title = stringResource(R.string.initial_setup_translation_languages_title),
            subTitle = stringResource(R.string.initial_setup_translation_languages_sub_title)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error card
        if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        LanguagePickerCard(
            title = stringResource(R.string.source_language),
            subtitle = stringResource(R.string.source_language_subtitle),
            selectedLanguage = sourceLanguage,
            onLanguageSelected = { sourceLanguage = it },
            emoji = "📚"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LanguagePickerCard(
            title = stringResource(R.string.target_language),
            subtitle = stringResource(R.string.target_language_subtitle),
            selectedLanguage = targetLanguage,
            onLanguageSelected = { targetLanguage = it },
            emoji = "🎯"
        )

        Spacer(modifier = Modifier.weight(1f))

        // Validation warning
        if (showValidationError) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = stringResource(R.string.error_same_languages),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(getGreeting(sourceLanguage.code))
                        append(sourceLanguage.flagEmoji)
                        append(" → ")
                        append(getGreeting(targetLanguage.code))
                        append(targetLanguage.flagEmoji)
                    },
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLanguagesSelected(sourceLanguage, targetLanguage) },
            enabled = !isLoading && !showValidationError,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.continue_button),
                    fontSize = 18.sp
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