package com.example.crm.service;

import com.example.crm.model.Note;
import com.example.crm.model.Task;
import com.example.crm.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskAndNoteServiceTest {

    private final LocalDate today = LocalDate.of(2026, 10, 1);

    @Test
    void findTasksByAssignee() {
        TaskService service = new TaskService(new InMemoryRepository<>());
        service.add(new Task("T1", 1L, 5L, today));
        service.add(new Task("T2", 1L, 6L, today));

        assertEquals(1, service.findByAssigneeId(5L).size());
    }

    @Test
    void overdueTasksDoNotIncludeDoneOrFutureTasks() {
        TaskService service = new TaskService(new InMemoryRepository<>());
        service.add(new Task("Late", 1L, 5L, today.minusDays(1)));
        service.add(new Task("Late but done", 1L, 5L, today.minusDays(2)));
        service.add(new Task("Today", 1L, 5L, today));
        service.add(new Task("Future", 1L, 5L, today.plusDays(1)));
        service.markDone(2L);

        assertEquals(1, service.findOverdue(today).size());
        assertEquals("Late", service.findOverdue(today).get(0).getTitle());
    }

    @Test
    void markDone() {
        TaskService service = new TaskService(new InMemoryRepository<>());
        service.add(new Task("T1", 1L, 5L, today));

        assertTrue(service.markDone(1L));
        assertTrue(service.findById(1L).get().isDone());
        assertFalse(service.markDone(42L));
    }

    @Test
    void findNotesByDeal() {
        NoteService service = new NoteService(new InMemoryRepository<>());
        service.add(new Note(1L, "first"));
        service.add(new Note(1L, "second"));
        service.add(new Note(2L, "other"));

        assertEquals(2, service.findByDealId(1L).size());
    }
}
