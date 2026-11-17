package com.example.crm.exception;

public class ResourceInUseException extends CrmException {
    public ResourceInUseException(String entity, String dependency) {
        super("Нельзя удалить " + entity + ": существуют " + dependency + ".", "Сначала удалите или переназначьте связанные записи: " + dependency + ".");
    }
}
