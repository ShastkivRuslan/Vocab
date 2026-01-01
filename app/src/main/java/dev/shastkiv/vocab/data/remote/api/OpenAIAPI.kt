package dev.shastkiv.vocab.data.remote.api

import dev.shastkiv.vocab.data.remote.dto.ChatCompletionRequest
import dev.shastkiv.vocab.data.remote.dto.ChatCompletionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIAPI {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body body: ChatCompletionRequest
    ): Response<ChatCompletionResponse>
}