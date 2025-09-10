package com.shastkiv.vocab.data.remote.client

import com.shastkiv.vocab.BuildConfig
import com.shastkiv.vocab.data.remote.api.OpenAIAPI
import com.shastkiv.vocab.data.remote.dto.ChatCompletionRequest
import com.shastkiv.vocab.domain.model.WordData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAIClient @Inject constructor(
    private val openAiApi: OpenAIAPI
) {
    suspend fun fetchWordInfo(
        word: String,
        sourceLanguageName: String,
        targetLanguageName: String
    ): Result<WordData> {
        val request = createChatCompletionRequest(word, sourceLanguageName, targetLanguageName)
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

    private fun createChatCompletionRequest(
        input: String,
        sourceLanguage: String,
        targetLanguage: String
    ): ChatCompletionRequest {
        val messages = listOf(
            ChatCompletionRequest.Message(
                role = "system",
                content = "You are a language assistant expert in linguistics. Your task is to provide structured data about words for a language learner. You must respond only with a single, valid JSON object and nothing else. The JSON structure is: " +
                        "{originalWord: string, translation: \"string (1-3 comma-separated translations)\", transcription: string, partOfSpeech: string, level: string, usageInfo: string, examples: [{sentence: string, translation: string}]}"
            ),
            ChatCompletionRequest.Message(
                role = "user",
                content = "Provide a complete JSON object for the $sourceLanguage word: \"$input\".\n" +
                        "Follow these detailed instructions for each field, with all translations targeting the $targetLanguage language:\n" +
                        "- `translation`: Provide 1-3 common $targetLanguage translations as a single string, separated by commas.\n" +
                        "- `level`: Assign the word's difficulty level strictly according to the CEFR scale. The value MUST BE one of: \"A1\", \"A2\", \"B1\", \"B2\", \"C1\", or \"C2\".\n" +
                        "- `usageInfo`: Create a single, well-formatted $targetLanguage string. It MUST include the following, clearly labeled in $targetLanguage:\n" +
                        "  1. 'Синоніми:' (or its equivalent in $targetLanguage): List 2-3 common synonyms, each formatted as '$sourceLanguage word – $targetLanguage translation'.\n" +
                        "  2. 'Форми слова:' (or its equivalent in $targetLanguage): List the main grammatical forms, each formatted as '$sourceLanguage word – $targetLanguage translation'.\n" +
                        "  3. 'Примітка:' (or its equivalent in $targetLanguage): Add a brief usage note (1-2 sentences), ONLY if the word is not simple. If the word is simple, omit this label and note.\n" +
                        "- `examples`: Provide exactly 3 distinct, simple example sentences in $sourceLanguage, each with its $targetLanguage translation.\n" +
                        "- Provide all other fields (`originalWord`, `transcription`, `partOfSpeech`) as specified in the system prompt.\n" +
                        "Your entire response must be only the JSON object."
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