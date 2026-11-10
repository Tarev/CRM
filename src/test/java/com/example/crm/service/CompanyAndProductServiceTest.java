package com.example.crm.service;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.model.Company;
import com.example.crm.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyAndProductServiceTest extends MockRepositoriesTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ProductService productService;

    @Test
    void findByCityWithBlankCityReturnsAllCompanies() {
        when(companyRepository.findAll()).thenReturn(List.of(new Company("A", "Moscow")));

        assertEquals(1, companyService.findByCity("  ").size());
        verify(companyRepository, never()).findByCityIgnoreCase(any());
    }

    @Test
    void findByCityUsesRepository() {
        when(companyRepository.findByCityIgnoreCase("Moscow")).thenReturn(List.of(new Company("A", "Moscow")));

        assertEquals(1, companyService.findByCity(" Moscow ").size());
    }

    @Test
    void createCompanyIgnoresIdFromRequest() {
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Company company = new Company("A", "Moscow");
        company.setId(77L);

        Company saved = companyService.create(company);

        assertNull(saved.getId());
    }

    @Test
    void updateMissingCompanyReturnsEmpty() {
        when(companyRepository.existsById(5L)).thenReturn(false);

        assertTrue(companyService.update(5L, new Company("A", "Moscow")).isEmpty());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateCompanySetsIdFromPath() {
        when(companyRepository.existsById(5L)).thenReturn(true);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Company> result = companyService.update(5L, new Company("B", "Kazan"));

        assertEquals(5L, result.get().getId());
    }

    @Test
    void deleteMissingCompanyReturnsFalse() {
        when(companyRepository.existsById(1L)).thenReturn(false);

        assertFalse(companyService.deleteById(1L));
        verify(companyRepository, never()).deleteById(any());
    }

    @Test
    void deleteExistingCompany() {
        when(companyRepository.existsById(1L)).thenReturn(true);

        assertTrue(companyService.deleteById(1L));
        verify(companyRepository).deleteById(1L);
    }

    @Test
    void productSearchReplacesNullNameWithEmptyString() {
        Product product = new Product("A-1", "CRM", new BigDecimal("100"));
        when(productRepository.search("", new BigDecimal("500"))).thenReturn(List.of(product));

        assertEquals(1, productService.search(null, new BigDecimal("500")).size());
    }
}
