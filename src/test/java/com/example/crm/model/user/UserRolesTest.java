package com.example.crm.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRolesTest {

    @Test
    void eachUserClassHasItsOwnRole() {
        User admin = new AdminUser("admin", "Admin");
        User manager = new ManagerUser("manager", "Manager", "Sales");
        User customer = new CustomerUser("customer", "Customer", 1L);

        assertEquals(UserRole.ADMIN, admin.getRole());
        assertEquals(UserRole.MANAGER, manager.getRole());
        assertEquals(UserRole.CUSTOMER, customer.getRole());
    }

    @Test
    void managerWithoutDepartmentIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ManagerUser("manager", "Manager", " "));
    }

    @Test
    void userWithoutLoginIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AdminUser("", "Admin"));
    }
}
