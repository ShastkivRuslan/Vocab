package dev.shastkiv.vocab.data.remote.client

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dev.shastkiv.vocab.data.remote.api.OpenAIAPI
import dev.shastkiv.vocab.data.remote.dto.ChatCompletionRequest
import dev.shastkiv.vocab.domain.model.WordData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIClient @Inject constructor(
    private val openAiApi: OpenAIAPI,
    private val remoteConfig: FirebaseRemoteConfig
) {
    suspend fun fetchWordInfo(
        word: String,
        sourceLanguageName: String,
        targetLanguageName: String
    ): Result<WordData> {
        val request = createChatCompletionRequest(word, sourceLanguageName, targetLanguageName)

        val remoteKey = remoteConfig.getString("openai_api_key")
        val apiKey = remoteKey.ifBlank { /*BuildConfig.API_KEY*/ }

        val authHeader = "Bearer $apiKey"

        return try {
            val response = openAiApi.getChatCompletion(authHeader, request)

            if (response.isSuccessful && response.body() != null) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                if (content.isNullOrBlank()) {
                    Result.failure(WordInfoException.EmptyContentException)
                } else {
                    val wordData = WordData.fromJson(content)
                    if (wordData != null) {
                        Result.success(wordData)
                    } else {
                        Result.failure(WordInfoException.ParsingException)
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Невідома помилка"
                Result.failure(WordInfoException.HttpApiError(response.code(), errorBody))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createChatCompletionRequest(
        input: String,
        sourceLanguage: String,
        targetLanguage: String
    ): ChatCompletionRequest {
        val messages = listOf(
            ChatCompletionRequest.Message(
                role = "system",
                content = """
                        You are a professional linguistic assistant . 
                        Your strict duty is to return data in a valid JSON format.
                        
                        CRITICAL RULES:
                        1. Always return ONLY a JSON object. No preamble, no markdown blocks.
                        2. LANGUAGE SCOPE: You MUST analyze the word ONLY within the context of the specified Source Language. Even if the word exists in other languages with different meanings, ignore them.
                        3. TYPO CORRECTION: If the input has a typo, find the closest word ONLY in the Source Language.
                        4. Use the CEFR scale (A1-C2) for the 'level' field.
                        5. Structure for 'usageInfo': Use '\n' for line breaks. Translate labels (Synonyms, Forms, Note) into the target language.
                        
                        JSON Schema:
                        {
                          "originalWord": "string",
                          "translation": "string",
                          "transcription": "string",
                          "partOfSpeech": "string",
                          "level": "string",
                          "usageInfo": "string",
                          "examples": [{"sentence": "string", "translation": "string"}]
                        }
                    """
            ),
            ChatCompletionRequest.Message(
                role = "user",
                content = """
                        Analyze the word/phrase: "$input" 
                        Source Language: $sourceLanguage
                        Target Language: $targetLanguage
                    
                        Instructions:
                        - 'translation': Provide the most accurate translation. Use 2-3 only if the word is multi-meaning and common. If one word covers it, use only one.
                        - 'partOfSpeech': Provide the name of the part of speech in $targetLanguage.
                        - 'usageInfo': Provide synonyms, grammatical forms, and a usage note if the word is complex. Everything in $targetLanguage.
                        - 'examples': Exactly 3 simple, high-frequency sentences in $sourceLanguage with translations in $targetLanguage.
                """
            )
        )
        return ChatCompletionRequest(
            model = "gpt-4o",
            messages = messages,
            maxTokens = 500,
            responseFormat = ChatCompletionRequest.ResponseFormat(type = "json_object")
        )
    }
}