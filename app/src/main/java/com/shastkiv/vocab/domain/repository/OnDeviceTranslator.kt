package com.shastkiv.vocab.domain.repository

interface OnDeviceTranslator {
    suspend fun translate(text: String, sourceLangCode: String, targetLangCode: String): String
}