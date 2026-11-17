package com.example.crm.exception;

public class DuplicateResourceException extends CrmException {
    public DuplicateResourceException(String field) {
        super("Значение поля " + field + " уже используется.", "Укажите другое значение поля " + field + ".");
    }
}
