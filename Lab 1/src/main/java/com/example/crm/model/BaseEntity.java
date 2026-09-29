package com.example.crm.model;

/**
 * Базовый класс всех сущностей: хранит идентификатор.
 */
public abstract class BaseEntity {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
