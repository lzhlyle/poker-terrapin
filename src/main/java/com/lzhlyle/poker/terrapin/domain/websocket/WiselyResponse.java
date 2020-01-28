package com.lzhlyle.poker.terrapin.domain.websocket;

public class WiselyResponse {
    private String message;

    public WiselyResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
