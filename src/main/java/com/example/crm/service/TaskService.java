package com.example.crm.service;

import com.example.crm.model.Task;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class TaskService extends AbstractService<Task> {

    public TaskService(EntityRepository<Task> repository) {
        super(repository);
    }

    public List<Task> findByAssigneeId(Long assigneeId) {
        List<Task> result = new ArrayList<>();
        for (Task task : findAll()) {
            if (assigneeId != null && assigneeId.equals(task.getAssigneeId())) {
                result.add(task);
            }
        }
        return result;
    }

    /** Невыполненные задачи, срок которых прошел до указанной даты. */
    public List<Task> findOverdue(LocalDate today) {
        List<Task> result = new ArrayList<>();
        for (Task task : findAll()) {
            if (!task.isDone() && task.getDeadline().isBefore(today)) {
                result.add(task);
            }
        }
        return result;
    }

    /** Отмечает задачу выполненной. Возвращает false, если задача не найдена. */
    public boolean markDone(Long id) {
        Optional<Task> task = findById(id);
        if (task.isEmpty()) {
            return false;
        }
        task.get().setDone(true);
        return true;
    }
}
