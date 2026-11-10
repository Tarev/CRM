package com.example.crm.service;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.dto.NoteRequest;
import com.example.crm.dto.TaskRequest;
import com.example.crm.dto.UserRequest;
import com.example.crm.model.Client;
import com.example.crm.model.Deal;
import com.example.crm.model.Note;
import com.example.crm.model.Task;
import com.example.crm.model.user.AdminUser;
import com.example.crm.model.user.CustomerUser;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.User;
import com.example.crm.model.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskNoteUserServiceTest extends MockRepositoriesTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    private TaskRequest taskRequest(Long dealId) {
        TaskRequest request = new TaskRequest();
        request.setTitle("Call");
        request.setDeadline(LocalDate.now().plusDays(1));
        request.setAssigneeId(2L);
        request.setDealId(dealId);
        return request;
    }

    private UserRequest userRequest(UserRole role, Long clientId) {
        UserRequest request = new UserRequest();
        request.setLogin("login");
        request.setFullName("Full Name");
        request.setRole(role);
        request.setClientId(clientId);
        return request;
    }

    // ---------- tasks

    @Test
    void createTaskWithoutDeal() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new ManagerUser("m", "M")));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Task> result = taskService.create(taskRequest(null));

        assertTrue(result.isPresent());
        assertNull(result.get().getDeal());
        assertFalse(result.get().isDone());
    }

    @Test
    void createTaskWithUnknownAssigneeReturnsEmpty() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertTrue(taskService.create(taskRequest(null)).isEmpty());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTaskWithUnknownDealReturnsEmpty() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new ManagerUser("m", "M")));
        when(dealRepository.findById(8L)).thenReturn(Optional.empty());

        assertTrue(taskService.create(taskRequest(8L)).isEmpty());
    }

    @Test
    void markDone() {
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertTrue(taskService.markDone(1L).get().isDone());
    }

    @Test
    void markDoneOfMissingTaskReturnsEmpty() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertTrue(taskService.markDone(1L).isEmpty());
    }

    @Test
    void findOverdueAsksRepositoryWithGivenDate() {
        LocalDate today = LocalDate.of(2026, 10, 1);
        when(taskRepository.findByDoneFalseAndDeadlineBefore(today)).thenReturn(List.of(new Task()));

        assertEquals(1, taskService.findOverdue(today).size());
    }

    // ---------- notes

    @Test
    void createNoteSetsCreationTime() {
        NoteRequest request = new NoteRequest();
        request.setDealId(1L);
        request.setText("text");
        when(dealRepository.findById(1L)).thenReturn(Optional.of(new Deal()));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Note> result = noteService.create(request);

        assertNotNull(result.get().getCreatedAt());
        assertEquals("text", result.get().getText());
    }

    @Test
    void createNoteForUnknownDealReturnsEmpty() {
        NoteRequest request = new NoteRequest();
        request.setDealId(1L);
        request.setText("text");
        when(dealRepository.findById(1L)).thenReturn(Optional.empty());

        assertTrue(noteService.create(request).isEmpty());
    }

    @Test
    void findNotesByDeal() {
        when(noteRepository.findByDealIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(new Note()));

        assertEquals(1, noteService.findByDealId(1L).size());
    }

    // ---------- users

    @Test
    void createUserCreatesClassByRole() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertTrue(userService.create(userRequest(UserRole.ADMIN, null)).get() instanceof AdminUser);
        assertTrue(userService.create(userRequest(UserRole.MANAGER, null)).get() instanceof ManagerUser);
        assertTrue(userService.create(userRequest(UserRole.CUSTOMER, null)).get() instanceof CustomerUser);
    }

    @Test
    void createCustomerLinksClient() {
        Client client = new Client();
        client.setId(4L);
        when(clientRepository.findById(4L)).thenReturn(Optional.of(client));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerUser customer = (CustomerUser) userService.create(userRequest(UserRole.CUSTOMER, 4L)).get();

        assertEquals(4L, customer.getClient().getId());
    }

    @Test
    void createCustomerWithUnknownClientReturnsEmpty() {
        when(clientRepository.findById(4L)).thenReturn(Optional.empty());

        assertTrue(userService.create(userRequest(UserRole.CUSTOMER, 4L)).isEmpty());
    }

    @Test
    void adminCannotHaveClient() {
        assertTrue(userService.create(userRequest(UserRole.ADMIN, 4L)).isEmpty());
        verify(userRepository, never()).save(any());
    }

    @Test
    void roleCannotBeChanged() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new ManagerUser("m", "M")));

        assertTrue(userService.update(1L, userRequest(UserRole.ADMIN, null)).isEmpty());
    }

    @Test
    void updateUserChangesName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new ManagerUser("m", "M")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.update(1L, userRequest(UserRole.MANAGER, null)).get();

        assertEquals("Full Name", result.getFullName());
    }

    @Test
    void findByRoleWithoutRoleReturnsAll() {
        when(userRepository.findAll()).thenReturn(List.of(new AdminUser("a", "A"), new ManagerUser("m", "M")));

        assertEquals(2, userService.findByRole(null).size());
    }

    @Test
    void findByRoleUsesRoleName() {
        when(userRepository.findByRole("MANAGER")).thenReturn(List.of(new ManagerUser("m", "M")));

        assertEquals(1, userService.findByRole(UserRole.MANAGER).size());
    }
}
