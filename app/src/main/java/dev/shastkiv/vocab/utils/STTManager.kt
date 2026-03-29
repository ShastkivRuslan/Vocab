package dev.shastkiv.vocab.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class STTManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _sttResult = MutableSharedFlow<STTResult>(extraBufferCapacity = 1)
    val sttResult = _sttResult.asSharedFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel = _rmsLevel.asStateFlow()

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(buildListener())
        }
    }

    suspend fun startListening(languageCode: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        withContext(Dispatchers.Main) {
            _isListening.value = true
            _isReady.value = false
            _rmsLevel.value = 0f
            speechRecognizer?.startListening(intent)
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
        _isReady.value = false
        _rmsLevel.value = 0f
    }

    private fun buildListener() = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            _isReady.value = true
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {
            _rmsLevel.value = rmsdB
        }

        override fun onEndOfSpeech() {
            _isReady.value = false
            _rmsLevel.value = 0f
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull()
            _isListening.value = false
            _isReady.value = false
            _rmsLevel.value = 0f
            if (!text.isNullOrBlank()) {
                _sttResult.tryEmit(STTResult.Success(text))
            } else {
                _sttResult.tryEmit(STTResult.Empty)
            }
        }

        override fun onError(error: Int) {
            _isListening.value = false
            _isReady.value = false
            _rmsLevel.value = 0f
            _sttResult.tryEmit(STTResult.Error(error))
        }

        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    fun shutdown() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}

sealed class STTResult {
    data class Success(val text: String) : STTResult()
    data object Empty : STTResult()
    data class Error(val code: Int) : STTResult()
}