package com.example.crm.exception;

public class EntityNotFoundException extends CrmException {
    public EntityNotFoundException(String entity, Long id) {
        super(entity + " с id=" + id + " не найден(а).", "Проверьте идентификатор или запросите список объектов.");
    }
}
