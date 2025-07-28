package com.example.learnwordstrainer.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatCompletionRequest {
    private String model;
    private List<Message> messages;

    @SerializedName("max_tokens")
    private int maxTokens;

    private Double temperature;

    @SerializedName("top_p")
    private Double topP;

    private String user;

    @SerializedName("response_format")
    private ResponseFormat responseFormat;

    public ChatCompletionRequest() {}

    public void setModel(String model) {
        this.model = model;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setResponseFormat(ResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public static class ResponseFormat {
        private String type;

        public ResponseFormat(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
