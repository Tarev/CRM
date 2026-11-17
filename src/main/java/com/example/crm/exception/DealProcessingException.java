package com.example.crm.exception;

public class DealProcessingException extends CrmException {
    public DealProcessingException(String message, Throwable cause) {
        super(message, "Результат обработки не сохранён. Повторите запрос; при повторном сбое обратитесь к администратору.", cause);
    }
}
