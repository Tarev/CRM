package com.example.crm.controller;

import com.example.crm.dto.TaskRequest;
import com.example.crm.model.Task;
import com.example.crm.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /** Если overdue=true, возвращает только просроченные невыполненные задачи (остальные фильтры не учитываются). */
    @GetMapping
    public List<Task> getAll(@RequestParam(required = false) Long assigneeId,
                             @RequestParam(required = false) Long dealId,
                             @RequestParam(required = false) Boolean done,
                             @RequestParam(required = false) Boolean overdue) {
        if (overdue != null && overdue) {
            return taskService.findOverdue(LocalDate.now());
        }
        return taskService.search(assigneeId, dealId, done);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        Optional<Task> found = taskService.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found.get());
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody TaskRequest request) {
        Optional<Task> created = taskService.create(request);
        if (created.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        if (!taskService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Optional<Task> updated = taskService.update(id, request);
        if (updated.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!taskService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /** Отмечает задачу выполненной. */
    @PatchMapping("/{id}/done")
    public ResponseEntity<Task> markDone(@PathVariable Long id) {
        Optional<Task> task = taskService.markDone(id);
        if (task.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task.get());
    }
}
