package com.example.learnwordstrainer.data.remote.api

import com.example.learnwordstrainer.data.remote.dto.ChatCompletionRequest
import com.example.learnwordstrainer.data.remote.dto.ChatCompletionResponse
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