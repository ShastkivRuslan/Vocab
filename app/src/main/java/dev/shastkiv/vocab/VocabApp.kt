package dev.shastkiv.vocab

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dev.shastkiv.vocab.domain.repository.LanguageRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class VocabApp : Application(), Configuration.Provider {

    @Inject
    lateinit var languageRepository: LanguageRepository

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        setupLanguageObserver()
    }

    private fun setupLanguageObserver() {
        applicationScope.launch {
            languageRepository.languageSettings
                .map { it.appLanguage.code }
                .distinctUntilChanged()
                .collect { languageCode ->
                    applyLanguage(languageCode)
                }
        }
    }

    private fun applyLanguage(languageCode: String) {
        try {
            val localeList = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(localeList)
        } catch (e: Exception) {
            Log.e("VocabApp", "Error applying language: $languageCode", e)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}