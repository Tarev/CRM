package com.example.crm.service;

import com.example.crm.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Общая логика всех сервисов: сохранение, поиск по id, список, удаление.
 * Специфичные операции добавляются в классах-наследниках.
 */
public abstract class AbstractService<T extends BaseEntity> {

    private final JpaRepository<T, Long> repository;

    protected AbstractService(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    /** Удаляет объект. Возвращает false, если объекта с таким id нет. */
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
