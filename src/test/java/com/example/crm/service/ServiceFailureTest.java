package com.example.crm.service;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.dto.UserRequest;
import com.example.crm.exception.BusinessRuleViolationException;
import com.example.crm.exception.DuplicateResourceException;
import com.example.crm.model.Company;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.UserRole;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceFailureTest extends MockRepositoriesTest {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;

    @Test
    void updateCanKeepOwnUniqueValue() {
        Company existing = new Company("A", "Moscow");
        existing.setId(1L);
        when(companyRepository.existsById(1L)).thenReturn(true);
        when(companyRepository.findByName("A")).thenReturn(Optional.of(existing));
        when(companyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Company result = companyService.update(1L, new Company("A", "Kazan"));
        assertEquals(1L, result.getId());
        assertEquals("Kazan", result.getCity());
        verify(companyRepository).save(any());
    }

    @Test
    void updateCannotTakeAnotherObjectsUniqueValue() {
        Company existing = new Company("A", "Moscow");
        existing.setId(2L);
        when(companyRepository.existsById(1L)).thenReturn(true);
        when(companyRepository.findByName("A")).thenReturn(Optional.of(existing));
        assertThrows(DuplicateResourceException.class, () -> companyService.update(1L, new Company("A", "Kazan")));
        verify(companyRepository, never()).save(any());
    }

    @Test
    void failedUserUpdateDoesNotMutateExistingUser() {
        ManagerUser existing = new ManagerUser("old-login", "Old Name");
        existing.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        UserRequest request = new UserRequest();
        request.setLogin("new-login");
        request.setFullName("New Name");
        request.setRole(UserRole.MANAGER);
        request.setClientId(7L);
        assertThrows(BusinessRuleViolationException.class, () -> userService.update(1L, request));
        assertEquals("old-login", existing.getLogin());
        assertEquals("Old Name", existing.getFullName());
        verify(userRepository, never()).save(any());
    }
}
