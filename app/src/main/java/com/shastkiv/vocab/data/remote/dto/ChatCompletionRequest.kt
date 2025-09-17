package com.shastkiv.vocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    val temperature: Double? = null,
    @SerializedName("top_p")
    val topP: Double? = null,
    val user: String? = null,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat? = null
) {
    data class Message(
        val role: String,
        val content: String
    )

    data class ResponseFormat(
        val type: String
    )
}