package com.site.dto;

import com.site.models.QrSession;

public class QrStatusResponse {
    private QrSession.Status status;
    private String authToken;

    public QrStatusResponse(QrSession.Status status, String authToken) {
        this.status = status;
        this.authToken = authToken;
    }

    public QrSession.Status getStatus() { return status; }
    public void setStatus(QrSession.Status status) { this.status = status; }
    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }
}