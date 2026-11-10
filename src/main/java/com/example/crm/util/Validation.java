package com.example.crm.util;

import java.math.BigDecimal;

/**
 * Простые проверки значений полей.
 */
public final class Validation {

    private Validation() {
    }

    public static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\" не должно быть пустым");
        }
        return value.trim();
    }

    public static BigDecimal requireNotNegative(BigDecimal value, String fieldName) {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\" должно быть числом без минуса");
        }
        return value;
    }

    public static <T> T requireNotNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\" не задано");
        }
        return value;
    }
}
