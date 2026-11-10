package com.example.crm.service;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.dto.DealRequest;
import com.example.crm.dto.ProcessResponse;
import com.example.crm.model.Client;
import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import com.example.crm.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DealServiceTest extends MockRepositoriesTest {

    @Autowired
    private DealService dealService;

    private DealRequest request() {
        DealRequest request = new DealRequest();
        request.setDealNumber("D-1");
        request.setTitle("Deal");
        request.setAmount(new BigDecimal("100"));
        request.setClientId(1L);
        request.setProductId(2L);
        return request;
    }

    private Deal deal(long id, String amount) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setAmount(new BigDecimal(amount));
        return deal;
    }

    @Test
    void createDealHasStatusNew() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(productRepository.findById(2L)).thenReturn(Optional.of(new Product()));
        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Deal> result = dealService.create(request());

        assertTrue(result.isPresent());
        assertEquals(DealStatus.NEW, result.get().getStatus());
    }

    @Test
    void createDealWithUnknownProductReturnsEmpty() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertTrue(dealService.create(request()).isEmpty());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void changeStatusSavesDeal() {
        Deal deal = deal(1L, "10");
        when(dealRepository.findById(1L)).thenReturn(Optional.of(deal));
        when(dealRepository.save(any(Deal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Deal> result = dealService.changeStatus(1L, DealStatus.WON);

        assertEquals(DealStatus.WON, result.get().getStatus());
    }

    @Test
    void changeStatusOfMissingDealReturnsEmpty() {
        when(dealRepository.findById(1L)).thenReturn(Optional.empty());

        assertTrue(dealService.changeStatus(1L, DealStatus.WON).isEmpty());
    }

    @Test
    void totalAmountSumsDealsWithStatus() {
        when(dealRepository.findByStatus(DealStatus.WON)).thenReturn(List.of(deal(1L, "100"), deal(2L, "250.50")));

        assertEquals(0, new BigDecimal("350.50").compareTo(dealService.getTotalAmount(DealStatus.WON)));
    }

    @Test
    void totalAmountWithoutDealsIsZero() {
        when(dealRepository.findByStatus(DealStatus.LOST)).thenReturn(List.of());

        assertEquals(0, BigDecimal.ZERO.compareTo(dealService.getTotalAmount(DealStatus.LOST)));
    }

    @Test
    void processNewDealsChangesStatusInSeveralThreadsAndSaves() {
        Deal first = deal(1L, "10");
        Deal second = deal(2L, "20");
        Deal third = deal(3L, "30");
        when(dealRepository.findByStatus(DealStatus.NEW)).thenReturn(List.of(first, second, third));

        ProcessResponse response = dealService.processNewDeals();

        assertEquals(3, response.getProcessedDeals());
        assertFalse(response.getThreads().isEmpty());
        for (String name : response.getThreads()) {
            assertTrue(name.startsWith("deal-worker-"));
        }
        assertEquals(DealStatus.IN_PROGRESS, first.getStatus());
        assertEquals(DealStatus.IN_PROGRESS, second.getStatus());
        assertEquals(DealStatus.IN_PROGRESS, third.getStatus());
        verify(dealRepository).saveAll(anyList());
    }

    @Test
    void searchPassesFiltersToRepository() {
        when(dealRepository.search(DealStatus.NEW, 1L, null, null)).thenReturn(List.of(deal(1L, "10")));

        assertEquals(1, dealService.search(DealStatus.NEW, 1L, null, null).size());
    }
}
