package com.example.crm.exception;

public class BusinessRuleViolationException extends CrmException {
    public BusinessRuleViolationException(String message, String suggestion) {
        super(message, suggestion);
    }
}
