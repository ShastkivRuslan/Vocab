package com.example.learnwordstrainer.data.remote.client

import com.example.learnwordstrainer.BuildConfig
import com.example.learnwordstrainer.data.remote.api.OpenAIAPI
import com.example.learnwordstrainer.data.remote.dto.ChatCompletionRequest
import com.example.learnwordstrainer.domain.model.WordData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIClient @Inject constructor(
    private val openAiApi: OpenAIAPI
) {
    suspend fun fetchWordInfo(word: String): Result<WordData> {
        val request = createChatCompletionRequest(word)
        val authHeader = "Bearer ${BuildConfig.API_KEY}"

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

    private fun createChatCompletionRequest(input: String): ChatCompletionRequest {
        val messages = listOf(
            ChatCompletionRequest.Message(
                role = "system",
                content = "You are a language assistant. Respond only with JSON in this format: " +
                        "{originalWord: string, translation: string, transcription: string, partOfSpeech: string, level : string, context: string,  examples: [{sentence: string, translation: string}]}"
            ),
            ChatCompletionRequest.Message(
                role = "user",
                content = "Word or phrase: \"$input\"\n" +
                        "Return original word, its part of speech, IPA transcription,level, translation to Ukrainian, context(\"Provide a 'Context of Use' description for the English word. Explain its formality, typical domains (like software engineering, sustainability), and common collocations. The description should be in Ukrainian.) and 3 simple English sentences that use this word or phrase in context with their Ukrainian translations.\n" +
                        "Respond only with valid JSON without any explanation."
            )
        )
        return ChatCompletionRequest(
            model = "gpt-4o",
            messages = messages,
            maxTokens = 350,
            responseFormat = ChatCompletionRequest.ResponseFormat(type = "json_object")
        )
    }
}