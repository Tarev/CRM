package com.example.crm.service;

import com.example.crm.dto.TaskRequest;
import com.example.crm.model.Deal;
import com.example.crm.model.Task;
import com.example.crm.model.user.User;
import com.example.crm.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService extends AbstractService<Task> {

    private final TaskRepository taskRepository;
    private final DealService dealService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, DealService dealService, UserService userService) {
        super(taskRepository, "Задача");
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

    /** Создает задачу. */
    public Task create(TaskRequest request) {
        return fillAndSave(new Task(), request);
    }

    /** Обновляет задачу. */
    public Task update(Long id, TaskRequest request) {
        Task task = getById(id);
        return fillAndSave(task, request);
    }

    /** Отмечает задачу выполненной. */
    public Task markDone(Long id) {
        Task task = getById(id);
        task.setDone(true);
        return save(task);
    }

    private Task fillAndSave(Task task, TaskRequest request) {
        User assignee = userService.requireReference(request.getAssigneeId());
        Deal deal = null;
        if (request.getDealId() != null) {
            deal = dealService.requireReference(request.getDealId());
        }
        task.setTitle(request.getTitle());
        task.setDeadline(request.getDeadline());
        task.setDone(request.getDone() != null && request.getDone());
        task.setDeal(deal);
        task.setAssignee(assignee);
        return save(task);
    }
}
