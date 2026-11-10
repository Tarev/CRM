package com.example.crm.controller;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.model.Client;
import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
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

class DealControllerTest extends MockRepositoriesTest {

    @Autowired
    private MockMvc mockMvc;

    private Deal deal(long id, String number, String amount) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setDealNumber(number);
        deal.setAmount(new BigDecimal(amount));
        return deal;
    }

    @Test
    void getDealsByStatus() throws Exception {
        when(dealRepository.search(DealStatus.NEW, null, null, null)).thenReturn(List.of(deal(1L, "D-1", "100")));

        mockMvc.perform(get("/api/deals?status=NEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dealNumber").value("D-1"));
    }

    @Test
    void getDealsByAmountRange() throws Exception {
        when(dealRepository.search(null, null, new BigDecimal("50"), new BigDecimal("500")))
                .thenReturn(List.of(deal(1L, "D-1", "100"), deal(2L, "D-2", "300")));

        mockMvc.perform(get("/api/deals?minAmount=50&maxAmount=500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createDealReturns201() throws Exception {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(productRepository.findById(2L)).thenReturn(Optional.of(new Product()));
        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dealNumber\":\"D-1\",\"title\":\"Deal\",\"amount\":100.50,\"clientId\":1,\"productId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.dealNumber").value("D-1"));
    }

    @Test
    void createDealWithNegativeAmountReturns400() throws Exception {
        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dealNumber\":\"D-1\",\"title\":\"Deal\",\"amount\":-1,\"clientId\":1,\"productId\":2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDealWithUnknownClientReturns400() throws Exception {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());
        when(productRepository.findById(2L)).thenReturn(Optional.of(new Product()));

        mockMvc.perform(post("/api/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dealNumber\":\"D-1\",\"title\":\"Deal\",\"amount\":10,\"clientId\":1,\"productId\":2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeStatus() throws Exception {
        when(dealRepository.findById(1L)).thenReturn(Optional.of(deal(1L, "D-1", "100")));
        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(patch("/api/deals/1/status?status=WON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WON"));
    }

    @Test
    void changeStatusOfMissingDealReturns404() throws Exception {
        when(dealRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/deals/1/status?status=WON"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeStatusToUnknownValueReturns400() throws Exception {
        mockMvc.perform(patch("/api/deals/1/status?status=WRONG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void totalByStatus() throws Exception {
        when(dealRepository.findByStatus(DealStatus.WON))
                .thenReturn(List.of(deal(1L, "D-1", "100"), deal(2L, "D-2", "50.5")));

        mockMvc.perform(get("/api/deals/total?status=WON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(150.5));
    }

    @Test
    void processNewDealsUsesSeveralThreads() throws Exception {
        when(dealRepository.findByStatus(DealStatus.NEW))
                .thenReturn(List.of(deal(1L, "D-1", "10"), deal(2L, "D-2", "20"), deal(3L, "D-3", "30")));

        mockMvc.perform(post("/api/deals/process-new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processedDeals").value(3))
                .andExpect(jsonPath("$.threads").isNotEmpty());

        verify(dealRepository).saveAll(any());
    }

    @Test
    void deleteDeal() throws Exception {
        when(dealRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/deals/1"))
                .andExpect(status().isNoContent());
    }
}
