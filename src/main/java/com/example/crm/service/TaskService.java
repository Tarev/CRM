package com.example.crm.service;

import com.example.crm.dto.TaskRequest;
import com.example.crm.model.Deal;
import com.example.crm.model.Task;
import com.example.crm.model.user.User;
import com.example.crm.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService extends AbstractService<Task> {

    private final TaskRepository taskRepository;
    private final DealService dealService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, DealService dealService, UserService userService) {
        super(taskRepository);
        this.taskRepository = taskRepository;
        this.dealService = dealService;
        this.userService = userService;
    }

    /** Поиск по исполнителю, сделке и признаку выполнения. Любой параметр можно не указывать (null). */
    public List<Task> search(Long assigneeId, Long dealId, Boolean done) {
        return taskRepository.search(assigneeId, dealId, done);
    }

    /** Невыполненные задачи, срок которых прошел к указанной дате. */
    public List<Task> findOverdue(LocalDate today) {
        return taskRepository.findByDoneFalseAndDeadlineBefore(today);
    }

    /** Создает задачу. Возвращает empty, если исполнитель или сделка не найдены. */
    public Optional<Task> create(TaskRequest request) {
        return fillAndSave(new Task(), request);
    }

    /** Обновляет задачу. Возвращает empty, если задача, исполнитель или сделка не найдены. */
    public Optional<Task> update(Long id, TaskRequest request) {
        Optional<Task> task = findById(id);
        if (task.isEmpty()) {
            return Optional.empty();
        }
        return fillAndSave(task.get(), request);
    }

    /** Отмечает задачу выполненной. Возвращает empty, если задача не найдена. */
    public Optional<Task> markDone(Long id) {
        Optional<Task> task = findById(id);
        if (task.isEmpty()) {
            return Optional.empty();
        }
        task.get().setDone(true);
        return Optional.of(save(task.get()));
    }

    private Optional<Task> fillAndSave(Task task, TaskRequest request) {
        Optional<User> assignee = userService.findById(request.getAssigneeId());
        if (assignee.isEmpty()) {
            return Optional.empty();
        }
        Deal deal = null;
        if (request.getDealId() != null) {
            Optional<Deal> found = dealService.findById(request.getDealId());
            if (found.isEmpty()) {
                return Optional.empty();
            }
            deal = found.get();
        }
        task.setTitle(request.getTitle());
        task.setDeadline(request.getDeadline());
        task.setDone(request.getDone() != null && request.getDone());
        task.setDeal(deal);
        task.setAssignee(assignee.get());
        return Optional.of(save(task));
    }
}
