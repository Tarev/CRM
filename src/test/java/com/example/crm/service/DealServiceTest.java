package com.example.crm.service;

import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import com.example.crm.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DealServiceTest {

    private DealService service;

    @BeforeEach
    void setUp() {
        service = new DealService(new InMemoryRepository<>());
        service.add(new Deal("Deal 1", 1L, 1L, new BigDecimal("100")));
        service.add(new Deal("Deal 2", 1L, 2L, new BigDecimal("250.50")));
        service.add(new Deal("Deal 3", 2L, 1L, new BigDecimal("300")));
    }

    @Test
    void findByStatus() {
        service.changeStatus(1L, DealStatus.WON);

        assertEquals(1, service.findByStatus(DealStatus.WON).size());
        assertEquals(2, service.findByStatus(DealStatus.NEW).size());
        assertTrue(service.findByStatus(DealStatus.LOST).isEmpty());
    }

    @Test
    void findByClientId() {
        assertEquals(2, service.findByClientId(1L).size());
        assertEquals(1, service.findByClientId(2L).size());
    }

    @Test
    void totalAmountByStatus() {
        service.changeStatus(1L, DealStatus.WON);
        service.changeStatus(2L, DealStatus.WON);

        assertEquals(0, new BigDecimal("350.50").compareTo(service.getTotalAmount(DealStatus.WON)));
        assertEquals(0, BigDecimal.ZERO.compareTo(service.getTotalAmount(DealStatus.LOST)));
    }

    @Test
    void changeStatusOfMissingDealReturnsFalse() {
        assertFalse(service.changeStatus(99L, DealStatus.WON));
    }
}
