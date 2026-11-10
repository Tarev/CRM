package com.example.crm.service;

import com.example.crm.dto.DealRequest;
import com.example.crm.dto.ProcessResponse;
import com.example.crm.model.Client;
import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import com.example.crm.model.Product;
import com.example.crm.processing.DealProcessor;
import com.example.crm.repository.DealRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DealService extends AbstractService<Deal> {

    private final DealRepository dealRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final DealProcessor dealProcessor;

    public DealService(DealRepository dealRepository, ClientService clientService,
                       ProductService productService, DealProcessor dealProcessor) {
        super(dealRepository);
        this.dealRepository = dealRepository;
        this.clientService = clientService;
        this.productService = productService;
        this.dealProcessor = dealProcessor;
    }

    /** Поиск по статусу, клиенту и диапазону суммы. Любой параметр можно не указывать (null). */
    public List<Deal> search(DealStatus status, Long clientId, BigDecimal minAmount, BigDecimal maxAmount) {
        return dealRepository.search(status, clientId, minAmount, maxAmount);
    }

    /** Создает сделку. Возвращает empty, если клиент или продукт не найдены. */
    public Optional<Deal> create(DealRequest request) {
        return fillAndSave(new Deal(), request);
    }

    /** Обновляет сделку. Возвращает empty, если сделка, клиент или продукт не найдены. */
    public Optional<Deal> update(Long id, DealRequest request) {
        Optional<Deal> deal = findById(id);
        if (deal.isEmpty()) {
            return Optional.empty();
        }
        return fillAndSave(deal.get(), request);
    }

    /** Меняет статус сделки. Возвращает empty, если сделка не найдена. */
    public Optional<Deal> changeStatus(Long id, DealStatus status) {
        Optional<Deal> deal = findById(id);
        if (deal.isEmpty()) {
            return Optional.empty();
        }
        deal.get().setStatus(status);
        return Optional.of(save(deal.get()));
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

    private Optional<Deal> fillAndSave(Deal deal, DealRequest request) {
        Optional<Client> client = clientService.findById(request.getClientId());
        Optional<Product> product = productService.findById(request.getProductId());
        if (client.isEmpty() || product.isEmpty()) {
            return Optional.empty();
        }
        deal.setDealNumber(request.getDealNumber());
        deal.setTitle(request.getTitle());
        deal.setAmount(request.getAmount());
        if (request.getStatus() != null) {
            deal.setStatus(request.getStatus());
        }
        deal.setClient(client.get());
        deal.setProduct(product.get());
        return Optional.of(save(deal));
    }
}
