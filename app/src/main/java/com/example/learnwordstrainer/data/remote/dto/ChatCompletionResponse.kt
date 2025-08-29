package com.example.learnwordstrainer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>
) {
    data class Choice(
        val index: Int,
        val message: Message,
        @SerializedName("finish_reason")
        val finishReason: String
    )

    data class Message(
        val role: String,
        val content: String?
    )
}