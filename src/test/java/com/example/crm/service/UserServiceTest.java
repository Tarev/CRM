package com.example.crm.service;

import com.example.crm.model.user.AdminUser;
import com.example.crm.model.user.CustomerUser;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.UserRole;
import com.example.crm.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(new InMemoryRepository<>());
        service.add(new AdminUser("admin", "Admin"));
        service.add(new ManagerUser("manager1", "Manager One", "Sales"));
        service.add(new ManagerUser("manager2", "Manager Two", "Support"));
        service.add(new CustomerUser("customer", "Customer", 1L));
    }

    @Test
    void findByRole() {
        assertEquals(2, service.findByRole(UserRole.MANAGER).size());
        assertEquals(1, service.findByRole(UserRole.ADMIN).size());
    }

    @Test
    void findByLoginIgnoresCase() {
        assertTrue(service.findByLogin("ADMIN").isPresent());
        assertFalse(service.findByLogin("nobody").isPresent());
    }

    @Test
    void duplicateLoginIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.add(new AdminUser("Admin", "Another")));
        assertEquals(4, service.findAll().size());
    }
}
