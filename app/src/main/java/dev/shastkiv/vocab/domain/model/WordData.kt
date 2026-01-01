package dev.shastkiv.vocab.domain.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

data class WordData(
    val originalWord: String,
    val translation: String,
    val transcription: String,
    val partOfSpeech: String,
    val level: String,
    val usageInfo: String,
    val examples: List<Example>
) {
    companion object {
        fun fromJson(jsonString: String): WordData? {
            return try {
                Gson().fromJson(jsonString, WordData::class.java)
            } catch (e: JsonSyntaxException) {

                null
            }
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}