package com.shastkiv.vocab.data.remote.client

import com.shastkiv.vocab.BuildConfig
import com.shastkiv.vocab.domain.model.WordData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiClient @Inject constructor() {

    private val jsonConfig = generationConfig {
        responseMimeType = "application/json"
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro-latest",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = jsonConfig
    )

    suspend fun fetchWordInfo(
        word: String,
        sourceLanguageName: String,
        targetLanguageName: String
    ): Result<WordData> {
        val prompt = createPrompt(word, sourceLanguageName, targetLanguageName)

        return try {
            val response = generativeModel.generateContent(prompt)
            val content = response.text

            if (content.isNullOrBlank()) {
                Result.failure(WordInfoException.EmptyContentException)
            } else {
                val cleanedJson = content.removePrefix("```json\n").removeSuffix("\n```")
                val wordData = WordData.fromJson(cleanedJson)

                if (wordData != null) {
                    Result.success(wordData)
                } else {
                    Result.failure(WordInfoException.ParsingException)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createPrompt(
        input: String,
        sourceLanguage: String,
        targetLanguage: String
    ): String {
        return """
        You are a language assistant expert in linguistics. Your task is to provide structured data about words for a language learner. You must respond only with a single, valid JSON object and nothing else. The JSON structure is: {originalWord: string, translation: "string (1-3 comma-separated translations)", transcription: string, partOfSpeech: string, level: string, usageInfo: string, examples: [{sentence: string, translation: string}]}

        ---

        Provide a complete JSON object for the $sourceLanguage word: "$input".
        Follow these detailed instructions for each field, with all translations targeting the $targetLanguage language:
        - `translation`: Provide 1-3 common $targetLanguage translations as a single string, separated by commas.
        - `usageInfo`: Create a single, well-formatted $targetLanguage string. It MUST include the following, clearly labeled in $targetLanguage:
          1. 'Синоніми:' (or its equivalent in $targetLanguage): List 2-3 common synonyms, each formatted as '$sourceLanguage word – $targetLanguage translation'.
          2. 'Форми слова:' (or its equivalent in $targetLanguage): List the main grammatical forms, each formatted as '$sourceLanguage word – $targetLanguage translation'.
          3. 'Примітка:' (or its equivalent in $targetLanguage): Add a brief usage note (1-2 sentences), ONLY if the word is not simple. If the word is simple, omit this label and note.
        - `examples`: Provide exactly 3 distinct, simple example sentences in $sourceLanguage, each with its $targetLanguage translation.
        - Provide all other fields as specified in the system prompt.
        Your entire response must be only the JSON object.
        """.trimIndent()
    }
}