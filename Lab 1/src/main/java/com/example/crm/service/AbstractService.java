package com.example.crm.service;

import com.example.crm.model.BaseEntity;
import com.example.crm.repository.EntityRepository;

import java.util.List;
import java.util.Optional;

/**
 * Общая логика всех сервисов: добавление, поиск по id, удаление, вывод списка.
 * Специфичный поиск добавляется в классах-наследниках.
 */
public abstract class AbstractService<T extends BaseEntity> {

    private final EntityRepository<T> repository;

    protected AbstractService(EntityRepository<T> repository) {
        this.repository = repository;
    }

    public T add(T entity) {
        return repository.save(entity);
    }

    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    /** Формирует текст со всеми объектами (по одному на строку). */
    public String describeAll() {
        return describe(findAll());
    }

    /** Формирует текст для произвольного списка объектов. */
    public String describe(List<T> items) {
        if (items.isEmpty()) {
            return "Список пуст";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                builder.append(System.lineSeparator());
            }
            builder.append(items.get(i));
        }
        return builder.toString();
    }
}
