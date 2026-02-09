package dev.shastkiv.vocab.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTSManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false


    private fun initializeTTS() {
        if (tts != null) return

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                Log.i("TTSManager", "TextToSpeech initialized successfully")
            } else {
                Log.e("TTSManager", "Failed to initialize TextToSpeech with status: $status")
                isInitialized = false
            }
        }
    }

    fun speak(text: String, languageCode: String?) {
        if (tts == null) {
            initializeTTS()

            Log.d("TTSManager", "TTS was not initialized, starting initialization...")
            return
        }

        if (!isInitialized) {
            Log.w("TTSManager", "TTS is not ready yet.")
            return
        }

        if (text.isBlank()) return

        val result = setLanguage(languageCode)

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTSManager", "Language $languageCode is not supported.")
            return
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun setLanguage(languageCode: String?): Int {
        val locale = if (!languageCode.isNullOrBlank()) {
            Locale(languageCode)
        } else {
            Locale.US
        }
        return tts?.setLanguage(locale) ?: TextToSpeech.ERROR
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}