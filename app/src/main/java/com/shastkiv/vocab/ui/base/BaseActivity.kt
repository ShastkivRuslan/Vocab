package com.shastkiv.vocab.ui.base

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.shastkiv.vocab.domain.repository.LanguageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var languageRepository: LanguageRepository

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(updateContextLocale(newBase))
    }

    private fun updateContextLocale(context: Context): Context {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Для старіших версій Android застосовуємо мову вручну
            try {
                val languageCode = runBlocking {
                    languageRepository.languageSettings.first()
                }.appLanguage.code

                val locale = Locale(languageCode)
                Locale.setDefault(locale)

                val config = Configuration(context.resources.configuration).apply {
                    setLocale(locale)
                }
                context.createConfigurationContext(config)
            } catch (e: Exception) {
                // Якщо щось пішло не так, повертаємо оригінальний контекст
                context
            }
        } else {
            context
        }
    }
}