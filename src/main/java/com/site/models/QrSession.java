package com.site.models;

import java.time.LocalDateTime;

public class QrSession {

    public enum Status {
        PENDING,   // Aguardando escaneamento
        CONFIRMED, // Confirmado pelo app
        EXPIRED    // Expirado
    }

    private Status status;
    private String userId;
    private String finalAuthToken; // Token JWT final a ser enviado para o frontend
    private final LocalDateTime createdAt;

    public QrSession() {
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFinalAuthToken() { return finalAuthToken; }
    public void setFinalAuthToken(String finalAuthToken) { this.finalAuthToken = finalAuthToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}