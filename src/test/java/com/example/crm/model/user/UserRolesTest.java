package com.example.crm.model.user;

import com.example.crm.model.Client;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserRolesTest {

    @Test
    void eachUserClassHasItsOwnRole() {
        User admin = new AdminUser("admin", "Admin");
        User manager = new ManagerUser("manager", "Manager");
        User customer = new CustomerUser("customer", "Customer", new Client());

        assertEquals(UserRole.ADMIN, admin.getRole());
        assertEquals(UserRole.MANAGER, manager.getRole());
        assertEquals(UserRole.CUSTOMER, customer.getRole());
    }

    @Test
    void everyRoleHasAccessDescription() {
        assertNotNull(new AdminUser("a", "A").getAccessDescription());
        assertNotNull(new ManagerUser("m", "M").getAccessDescription());
        assertNotNull(new CustomerUser("c", "C", null).getAccessDescription());
    }

    @Test
    void customerKeepsClient() {
        Client client = new Client();
        client.setEmail("c@mail.ru");

        CustomerUser customer = new CustomerUser("customer", "Customer", client);

        assertEquals("c@mail.ru", customer.getClient().getEmail());
    }
}
