package dev.shastkiv.vocab.ui.initialsetup.compose.content

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.domain.model.AvailableLanguages
import dev.shastkiv.vocab.ui.initialsetup.InitialSetupViewModel
import dev.shastkiv.vocab.ui.initialsetup.compose.components.AnimatedAppNameRow
import dev.shastkiv.vocab.ui.initialsetup.compose.components.AnimatedLanguagePrompt
import dev.shastkiv.vocab.ui.initialsetup.compose.components.LanguageCard

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

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedAppNameRow()
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedLanguagePrompt()
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                                    when(language.code) {
                                        "uk" -> "Мову змінено"
                                        "en" -> "Language changed"
                                        "pl" -> "Zmieniono język"
                                        "de" -> "Sprache geändert"
                                        "fr" -> "Langue changée"
                                        "cs" -> "Jazyk změněn"
                                        else -> "Language changed"
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        Button(
            onClick = onContinue,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, // Додаємо жирність для акценту
                    color = MaterialTheme.colorScheme.onPrimary // Явно вказуємо колір з теми
                )
            }
        }
    }
}
