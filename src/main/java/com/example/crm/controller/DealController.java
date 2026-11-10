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
import java.util.Optional;

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
        Optional<Deal> found = dealService.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found.get());
    }

    @PostMapping
    public ResponseEntity<Deal> create(@Valid @RequestBody DealRequest request) {
        Optional<Deal> created = dealService.create(request);
        if (created.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deal> update(@PathVariable Long id, @Valid @RequestBody DealRequest request) {
        if (!dealService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Optional<Deal> updated = dealService.update(id, request);
        if (updated.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!dealService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /** Меняет только статус сделки. */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Deal> changeStatus(@PathVariable Long id, @RequestParam DealStatus status) {
        Optional<Deal> deal = dealService.changeStatus(id, status);
        if (deal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deal.get());
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
