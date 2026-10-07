package com.example.crm.repository;

import com.example.crm.model.BaseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище сущностей.
 */
public interface EntityRepository<T extends BaseEntity> {

    /** Сохраняет сущность; если id не задан, выдает новый. */
    T save(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    /** Удаляет сущность, возвращает true, если она была найдена. */
    boolean deleteById(Long id);
}
