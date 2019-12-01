package com.botmasterzzz.auth.payload;

import java.util.Date;

public class AuthResponse {
    private boolean success;
    private String accessToken;
    private String tokenType = "Bearer";
    private long timestamp;
    private String message;

    public AuthResponse(String accessToken) {
        this.success = true;
        this.accessToken = accessToken;
        this.timestamp = new Date().getTime();
    }

    public AuthResponse(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
