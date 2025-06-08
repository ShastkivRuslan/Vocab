package com.example.learnwordstrainer.integration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenAIAPI {
    @POST("v1/chat/completions")
    Call<ChatCompletionResponse> getChatCompletion(
            @Header("Authorization") String authHeader,
            @Body ChatCompletionRequest body
    );
}

