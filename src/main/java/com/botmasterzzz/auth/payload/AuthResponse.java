package com.botmasterzzz.auth.payload;

import java.util.Date;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long timestamp;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
        this.timestamp = new Date().getTime();
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
}
