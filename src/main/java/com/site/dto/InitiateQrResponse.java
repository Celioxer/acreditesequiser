package com.site.dto;

public class InitiateQrResponse {
    private String sessionToken;

    public InitiateQrResponse(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
}