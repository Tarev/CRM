package com.example.crm.repository;

import com.example.crm.model.BaseEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Хранит сущности в Map (ключ - id). LinkedHashMap сохраняет порядок добавления.
 * Методы synchronized, чтобы безопасно работать из нескольких потоков.
 */
public class InMemoryRepository<T extends BaseEntity> implements EntityRepository<T> {

    private final Map<Long, T> storage = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public synchronized T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Нельзя сохранить null");
        }
        if (entity.getId() == null) {
            entity.setId(nextId);
            nextId++;
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public synchronized Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public synchronized List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public synchronized boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}
