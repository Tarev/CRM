package com.example.crm.service;

import com.example.crm.model.Product;
import com.example.crm.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductServiceTest {

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(new InMemoryRepository<>());
        service.add(new Product("CRM license", new BigDecimal("15000")));
        service.add(new Product("Support", new BigDecimal("5000")));
        service.add(new Product("Training", new BigDecimal("8000")));
    }

    @Test
    void findByName() {
        assertEquals(1, service.findByName("support").size());
    }

    @Test
    void findWithPriceUpToIncludesBoundary() {
        assertEquals(2, service.findWithPriceUpTo(new BigDecimal("8000")).size());
    }
}
