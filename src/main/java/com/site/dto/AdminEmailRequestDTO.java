package com.site.dto;

import java.util.List;

public class AdminEmailRequestDTO {
    private List<Long> userIds;
    private String subject;
    private String body;

    // Getters e Setters
    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}