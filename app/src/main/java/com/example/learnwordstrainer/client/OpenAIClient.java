package com.example.learnwordstrainer.client;

import com.example.learnwordstrainer.BuildConfig;
import com.example.learnwordstrainer.integration.ChatCompletionRequest;
import com.example.learnwordstrainer.integration.ChatCompletionResponse;
import com.example.learnwordstrainer.integration.OpenAIAPI;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIClient {
    private static final String BASE_URL = "https://api.openai.com/";

    private final OpenAIAPI service;

    public OpenAIClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(OpenAIAPI.class);
    }

    public void fetchExamplesFromGPT(String word, Callback<ChatCompletionResponse> callback) {
        List<ChatCompletionRequest.Message> messages = new ArrayList<>();
        messages.add(new ChatCompletionRequest.Message("system",
                "Respond only with JSON: {translation:..., transcription:..., partOfSpeech:..., examples:[{sentence:..., translation:...}]}"));
        messages.add(new ChatCompletionRequest.Message(
                "user",
                String.format(
                        "Word: %s Give part of speech, IPA transcription, 3 simple English sentences"
                                + " + Ukrainian translations. JSON only.",
                        word)
                )
        );

        ChatCompletionRequest request = new ChatCompletionRequest();
        //request.setModel("gpt-3.5-turbo");
        request.setModel("gpt-4.1-mini");
        request.setMessages(messages);
        request.setMaxTokens(250);

        String authHeader = "Bearer " + BuildConfig.API_KEY;;

        service.getChatCompletion(authHeader, request).enqueue(callback);
    }
}