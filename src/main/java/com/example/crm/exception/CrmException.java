package com.example.crm.exception;

import lombok.Getter;

/** Ошибка предметной области с рекомендацией для пользователя. */
@Getter
public abstract class CrmException extends RuntimeException {
    private final String suggestion;

    protected CrmException(String message, String suggestion) {
        super(message);
        this.suggestion = suggestion;
    }

    protected CrmException(String message, String suggestion, Throwable cause) {
        super(message, cause);
        this.suggestion = suggestion;
    }
}
