package com.example.crm.service;

import com.example.crm.model.DealStatus;
import com.example.crm.model.Deal;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class DealService extends AbstractService<Deal> {

    public DealService(EntityRepository<Deal> repository) {
        super(repository);
    }

    public List<Deal> findByStatus(DealStatus status) {
        List<Deal> result = new ArrayList<>();
        for (Deal deal : findAll()) {
            if (deal.getStatus() == status) {
                result.add(deal);
            }
        }
        return result;
    }

    public List<Deal> findByClientId(Long clientId) {
        List<Deal> result = new ArrayList<>();
        for (Deal deal : findAll()) {
            if (clientId != null && clientId.equals(deal.getClientId())) {
                result.add(deal);
            }
        }
        return result;
    }

    /** Сумма всех сделок с указанным статусом. */
    public BigDecimal getTotalAmount(DealStatus status) {
        BigDecimal total = BigDecimal.ZERO;
        for (Deal deal : findByStatus(status)) {
            total = total.add(deal.getAmount());
        }
        return total;
    }

    /** Меняет статус сделки. Возвращает false, если сделка не найдена. */
    public boolean changeStatus(Long id, DealStatus status) {
        Optional<Deal> deal = findById(id);
        if (deal.isEmpty()) {
            return false;
        }
        deal.get().setStatus(status);
        return true;
    }
}
