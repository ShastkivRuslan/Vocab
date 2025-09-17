package com.shastkiv.vocab.ui.base

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shastkiv.vocab.domain.repository.LanguageRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var languageRepository: LanguageRepository

    private var localeUpdated = false

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)

        // Для Android 13+ система сама керує локалізацією
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return
        }
    }

    override fun onStart() {
        super.onStart()

        // Оновлюємо локаль після старту активності
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !localeUpdated) {
            updateLocaleAsync()
        }
    }

    private fun updateLocaleAsync() {
        lifecycleScope.launch {
            try {
                val languageSettings = languageRepository.getLatestLanguageSettings()
                val languageCode = languageSettings.appLanguage.code

                val locale = Locale(languageCode)
                if (Locale.getDefault().language != locale.language) {
                    Locale.setDefault(locale)

                    val config = Configuration(resources.configuration).apply {
                        setLocale(locale)
                    }

                    resources.updateConfiguration(config, resources.displayMetrics)
                    localeUpdated = true

                    // Пересоздаем активність для применения нового языка
                    recreate()
                }
            } catch (e: Exception) {
                // Логуємо помилку, але не крашимо додаток
                e.printStackTrace()
            }
        }
    }
}