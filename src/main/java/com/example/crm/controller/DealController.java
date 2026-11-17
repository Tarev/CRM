package com.example.crm.controller;

import com.example.crm.dto.DealRequest;
import com.example.crm.model.Deal;
import com.example.crm.dto.ProcessResponse;
import com.example.crm.model.DealStatus;
import com.example.crm.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @GetMapping
    public List<Deal> getAll(@RequestParam(required = false) DealStatus status,
                             @RequestParam(required = false) Long clientId,
                             @RequestParam(required = false) BigDecimal minAmount,
                             @RequestParam(required = false) BigDecimal maxAmount) {
        return dealService.search(status, clientId, minAmount, maxAmount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deal> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dealService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Deal> create(@Valid @RequestBody DealRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deal> update(@PathVariable Long id, @Valid @RequestBody DealRequest request) {
        return ResponseEntity.ok(dealService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dealService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Меняет только статус сделки. */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Deal> changeStatus(@PathVariable Long id, @RequestParam DealStatus status) {
        return ResponseEntity.ok(dealService.changeStatus(id, status));
    }

    /** Сумма всех сделок с указанным статусом. */
    @GetMapping("/total")
    public BigDecimal getTotal(@RequestParam DealStatus status) {
        return dealService.getTotalAmount(status);
    }

    /** Многопоточная обработка новых сделок: NEW переходят в IN_PROGRESS. */
    @PostMapping("/process-new")
    public ProcessResponse processNew() {
        return dealService.processNewDeals();
    }
}
