package com.example.crm.exception;

public class DataConflictException extends CrmException {
    public DataConflictException() {
        super("Данные нарушают ограничения базы данных.", "Проверьте уникальность значений и связи с другими объектами.");
    }
}
