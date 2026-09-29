package com.example.crm.service;

import com.example.crm.model.Client;
import com.example.crm.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientServiceTest {

    private ClientService service;

    @BeforeEach
    void setUp() {
        service = new ClientService(new InMemoryRepository<>());
        service.add(new Client("Ivan Petrov", "ivan@mail.ru", "111", 1L));
        service.add(new Client("Anna Ivanova", "anna@mail.ru", "222", 2L));
        service.add(new Client("Oleg Smirnov", "oleg@mail.ru", "333", null));
    }

    @Test
    void addGivesId() {
        Client client = service.add(new Client("New", "n@mail.ru", "444", null));

        assertEquals(4L, client.getId());
        assertEquals(4, service.findAll().size());
    }

    @Test
    void findByNameIgnoresCase() {
        List<Client> found = service.findByName("IVAN");

        assertEquals(2, found.size());
    }

    @Test
    void findByNameReturnsEmptyListWhenNothingFound() {
        assertTrue(service.findByName("zzz").isEmpty());
        assertTrue(service.findByName(null).isEmpty());
    }

    @Test
    void findByCompanyId() {
        assertEquals(1, service.findByCompanyId(1L).size());
        assertTrue(service.findByCompanyId(null).isEmpty());
    }

    @Test
    void deleteRemovesClient() {
        assertTrue(service.deleteById(1L));

        assertFalse(service.findById(1L).isPresent());
        assertEquals(2, service.findAll().size());
    }

    @Test
    void describeAllContainsEveryClient() {
        String text = service.describeAll();

        assertTrue(text.contains("Ivan Petrov"));
        assertTrue(text.contains("Oleg Smirnov"));
    }

    @Test
    void describeEmptyList() {
        ClientService empty = new ClientService(new InMemoryRepository<>());

        assertEquals("Список пуст", empty.describeAll());
    }
}
