package com.example.crm.controller;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.model.Task;
import com.example.crm.model.user.ManagerUser;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest extends MockRepositoriesTest {

    @Autowired
    private MockMvc mockMvc;

    private Task task(long id, String title) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDeadline(LocalDate.now());
        return task;
    }

    @Test
    void getTasksByAssignee() throws Exception {
        when(taskRepository.search(2L, null, null)).thenReturn(List.of(task(1L, "Call")));

        mockMvc.perform(get("/api/tasks?assigneeId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Call"));
    }

    @Test
    void overdueFilterUsesSeparateQuery() throws Exception {
        when(taskRepository.findByDoneFalseAndDeadlineBefore(LocalDate.now())).thenReturn(List.of(task(5L, "Late")));

        mockMvc.perform(get("/api/tasks?overdue=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Late"));

        verify(taskRepository, never()).search(any(), any(), any());
    }

    @Test
    void createTaskReturns201() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new ManagerUser("m", "Manager")));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        String deadline = LocalDate.now().plusDays(5).toString();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Call\",\"deadline\":\"" + deadline + "\",\"assigneeId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.assignee.login").value("m"));
    }

    @Test
    void createTaskWithPastDeadlineReturns400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Call\",\"deadline\":\"2020-01-01\",\"assigneeId\":2}"))
                .andExpect(status().isBadRequest());

        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTaskWithUnknownAssigneeReturns400() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        String deadline = LocalDate.now().plusDays(5).toString();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Call\",\"deadline\":\"" + deadline + "\",\"assigneeId\":2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void markTaskDone() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task(1L, "Call")));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(patch("/api/tasks/1/done"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void markMissingTaskDoneReturns404() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/tasks/1/done"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask() throws Exception {
        when(taskRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
