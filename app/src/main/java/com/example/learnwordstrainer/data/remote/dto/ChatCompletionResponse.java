package com.example.learnwordstrainer.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Choice {
        private int index;
        private Message message;

        @SerializedName("finish_reason")
        private String finishReason;

        public Message getMessage() {
            return message;
        }
    }

    public static class Message {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}