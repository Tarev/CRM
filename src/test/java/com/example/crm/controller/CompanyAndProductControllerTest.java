package com.example.crm.controller;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.model.Company;
import com.example.crm.model.Product;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyAndProductControllerTest extends MockRepositoriesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCompaniesByCity() throws Exception {
        when(companyRepository.findByCityIgnoreCase("Moscow")).thenReturn(List.of(new Company("Romashka", "Moscow")));

        mockMvc.perform(get("/api/companies?city=Moscow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Romashka"));
    }

    @Test
    void createCompanyReturns201() throws Exception {
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Vector\",\"city\":\"Kazan\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value("Kazan"));
    }

    @Test
    void createCompanyWithEmptyNameReturns400() throws Exception {
        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"city\":\"Kazan\"}"))
                .andExpect(status().isBadRequest());

        verify(companyRepository, never()).save(any());
    }

    @Test
    void updateMissingCompanyReturns404() throws Exception {
        when(companyRepository.existsById(3L)).thenReturn(false);

        mockMvc.perform(put("/api/companies/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Vector\",\"city\":\"Kazan\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCompanyReturns204() throws Exception {
        when(companyRepository.existsById(3L)).thenReturn(true);

        mockMvc.perform(delete("/api/companies/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProductsByMaxPrice() throws Exception {
        when(productRepository.search("", new BigDecimal("500")))
                .thenReturn(List.of(new Product("A-1", "Support", new BigDecimal("300"))));

        mockMvc.perform(get("/api/products?maxPrice=500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].article").value("A-1"));
    }

    @Test
    void createProductReturns201() throws Exception {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"article\":\"A-1\",\"name\":\"Support\",\"price\":99.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(99.5));
    }

    @Test
    void createProductWithNegativePriceReturns400() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"article\":\"A-1\",\"name\":\"Support\",\"price\":-5}"))
                .andExpect(status().isBadRequest());
    }
}
