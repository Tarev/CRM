package com.example.crm.service;

import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import com.example.crm.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanyAndContactServiceTest {

    @Test
    void findCompanyByCityIgnoresCase() {
        CompanyService service = new CompanyService(new InMemoryRepository<>());
        service.add(new Company("A", "Moscow"));
        service.add(new Company("B", "Kazan"));

        assertEquals(1, service.findByCity("moscow").size());
        assertTrue(service.findByCity("Omsk").isEmpty());
    }

    @Test
    void findContactsByCompany() {
        ContactService service = new ContactService(new InMemoryRepository<>());
        service.add(new Contact("Maria", "Director", "1", 1L));
        service.add(new Contact("Pavel", "Accountant", "2", 1L));
        service.add(new Contact("Oleg", "Manager", "3", 2L));

        assertEquals(2, service.findByCompanyId(1L).size());
        assertEquals(1, service.findByCompanyId(2L).size());
    }
}
