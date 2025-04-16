package com.example.learnwordstrainer.integration;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatCompletionRequest {
    private String model;
    private List<Message> messages;

    @SerializedName("max_tokens")
    private int maxTokens;

    public ChatCompletionRequest() {
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
