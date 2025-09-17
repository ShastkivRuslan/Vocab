package com.shastkiv.vocab.data.repository

import com.shastkiv.vocab.domain.repository.OnDeviceTranslator
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OnDeviceTranslatorRepository @Inject constructor() : OnDeviceTranslator {

    override suspend fun translate(text: String, sourceLangCode: String, targetLangCode: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(targetLangCode)
            .build()
        val translator = Translation.getClient(options)

        return suspendCancellableCoroutine { continuation ->
            val conditions = DownloadConditions.Builder().build()
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            continuation.resume(translatedText)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }

            continuation.invokeOnCancellation {
                translator.close()
            }
        }
    }
}
