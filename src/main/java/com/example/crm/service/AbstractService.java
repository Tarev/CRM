package com.example.crm.service;

import com.example.crm.exception.DatabaseExceptionTranslator;
import com.example.crm.exception.DuplicateResourceException;
import com.example.crm.exception.EntityNotFoundException;
import com.example.crm.exception.InvalidReferenceException;
import com.example.crm.model.BaseEntity;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Общие операции и проверки. HTTP-ответы формируются в обработчиках исключений. */
public abstract class AbstractService<T extends BaseEntity> {
    private final JpaRepository<T, Long> repository;
    private final String entityName;

    protected AbstractService(JpaRepository<T, Long> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    public T save(T entity) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException exception) {
            throw DatabaseExceptionTranslator.translate(exception, false);
        }
    }

    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    public T getById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(entityName, id));
    }

    public T requireReference(Long id) {
        return findById(id).orElseThrow(() -> new InvalidReferenceException(entityName, id));
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    protected void requireExists(Long id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException(entityName, id);
        }
    }

    protected void requireUnique(Optional<? extends BaseEntity> existing, Long currentId, String field) {
        if (existing.isPresent() && (currentId == null || !Objects.equals(existing.get().getId(), currentId))) {
            throw new DuplicateResourceException(field);
        }
    }

    protected void checkCanDelete(Long id) {
        // Ограничения удаления определяются конкретным сервисом.
    }

    public void deleteById(Long id) {
        requireExists(id);
        checkCanDelete(id);
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException exception) {
            throw DatabaseExceptionTranslator.translate(exception, true);
        }
    }
}
