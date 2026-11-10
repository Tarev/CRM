package com.example.crm.model;

import com.example.crm.util.Validation;

import java.time.LocalDateTime;

public class Note extends BaseEntity {

    private final Long dealId;
    private final String text;
    private final LocalDateTime createdAt;

    public Note(Long dealId, String text) {
        this.dealId = Validation.requireNotNull(dealId, "dealId");
        this.text = Validation.requireNotBlank(text, "text");
        this.createdAt = LocalDateTime.now();
    }

    public Long getDealId() {
        return dealId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Заметка #" + getId() + " (сделка " + dealId + ", " + createdAt.toLocalDate() + "): " + text;
    }
}
