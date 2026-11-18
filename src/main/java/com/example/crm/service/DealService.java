package com.example.crm.service;

import com.example.crm.dto.DealRequest;
import com.example.crm.dto.ProcessResponse;
import com.example.crm.exception.BusinessRuleViolationException;
import com.example.crm.model.Client;
import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import com.example.crm.model.Product;
import com.example.crm.processing.DealProcessor;
import com.example.crm.repository.DealRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class DealService extends AbstractService<Deal> {

    private final DealRepository dealRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final DealProcessor dealProcessor;

    public DealService(DealRepository dealRepository, ClientService clientService,
                       ProductService productService, DealProcessor dealProcessor) {
        super(dealRepository, "Сделка");
        this.dealRepository = dealRepository;
        this.clientService = clientService;
        this.productService = productService;
        this.dealProcessor = dealProcessor;
    }

    /** Поиск по статусу, клиенту и диапазону суммы. Любой параметр можно не указывать (null). */
    public List<Deal> search(DealStatus status, Long clientId, BigDecimal minAmount, BigDecimal maxAmount) {
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new BusinessRuleViolationException(
                    "Минимальная сумма больше максимальной.", "Укажите minAmount не больше maxAmount.");
        }
        return dealRepository.search(status, clientId, minAmount, maxAmount);
    }

    /** Создает сделку. */
    public Deal create(DealRequest request) {
        return fillAndSave(new Deal(), request);
    }

    /** Обновляет сделку. */
    public Deal update(Long id, DealRequest request) {
        Deal deal = getById(id);
        return fillAndSave(deal, request);
    }

    /** Меняет статус сделки. */
    public Deal changeStatus(Long id, DealStatus status) {
        Deal deal = getById(id);
        deal.setStatus(status);
        return save(deal);
    }

    /** Сумма всех сделок с указанным статусом. */
    public BigDecimal getTotalAmount(DealStatus status) {
        BigDecimal total = BigDecimal.ZERO;
        for (Deal deal : dealRepository.findByStatus(status)) {
            total = total.add(deal.getAmount());
        }
        return total;
    }

    /** Берет новые сделки из БД, обрабатывает их в нескольких потоках и сохраняет результат. */
    public ProcessResponse processNewDeals() {
        List<Deal> newDeals = dealRepository.findByStatus(DealStatus.NEW);
        Set<String> threads = dealProcessor.process(newDeals);
        dealRepository.saveAll(newDeals);
        return new ProcessResponse(newDeals.size(), threads);
    }

    private Deal fillAndSave(Deal deal, DealRequest request) {
        Client client = clientService.requireReference(request.getClientId());
        Product product = productService.requireReference(request.getProductId());
        requireUnique(dealRepository.findByDealNumber(request.getDealNumber()), deal.getId(), "dealNumber");
        deal.setDealNumber(request.getDealNumber());
        deal.setTitle(request.getTitle());
        deal.setAmount(request.getAmount());
        if (request.getStatus() != null) {
            deal.setStatus(request.getStatus());
        }
        deal.setClient(client);
        deal.setProduct(product);
        return save(deal);
    }
}
