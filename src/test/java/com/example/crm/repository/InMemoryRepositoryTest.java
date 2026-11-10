package com.example.crm.repository;

import com.example.crm.model.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRepositoryTest {

    private InMemoryRepository<Company> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
    }

    @Test
    void saveAssignsIncreasingIds() {
        Company first = repository.save(new Company("A", "Moscow"));
        Company second = repository.save(new Company("B", "Kazan"));

        assertEquals(1L, first.getId());
        assertEquals(2L, second.getId());
    }

    @Test
    void saveSameEntityTwiceDoesNotDuplicate() {
        Company company = repository.save(new Company("A", "Moscow"));
        repository.save(company);

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findByIdReturnsSavedEntity() {
        Company saved = repository.save(new Company("A", "Moscow"));

        assertTrue(repository.findById(saved.getId()).isPresent());
        assertFalse(repository.findById(999L).isPresent());
    }

    @Test
    void deleteByIdRemovesEntity() {
        Company saved = repository.save(new Company("A", "Moscow"));

        assertTrue(repository.deleteById(saved.getId()));
        assertFalse(repository.deleteById(saved.getId()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void saveNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }
}
