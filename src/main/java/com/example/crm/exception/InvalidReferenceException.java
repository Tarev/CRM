package com.example.crm.exception;

public class InvalidReferenceException extends CrmException {
    public InvalidReferenceException(String entity, Long id) {
        super("Связанный объект: " + entity + " с id=" + id + " не найден(а).", "Укажите ID существующего связанного объекта.");
    }
}
