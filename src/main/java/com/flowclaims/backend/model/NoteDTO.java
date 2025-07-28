package com.flowclaims.backend.model;

public class NoteDTO {
    private Long id;
    private String text;
    private String createdAt;  // ISO date string

    public NoteDTO(Long id, String text, String createdAt) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

