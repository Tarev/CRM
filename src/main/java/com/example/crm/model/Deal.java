package com.example.crm.model;

import com.example.crm.util.Validation;

import java.math.BigDecimal;

public class Deal extends BaseEntity {

    private final String title;
    private final Long clientId;
    private final Long productId;
    private final BigDecimal amount;
    private DealStatus status;

    public Deal(String title, Long clientId, Long productId, BigDecimal amount) {
        this.title = Validation.requireNotBlank(title, "title");
        this.clientId = Validation.requireNotNull(clientId, "clientId");
        this.productId = Validation.requireNotNull(productId, "productId");
        this.amount = Validation.requireNotNegative(amount, "amount");
        this.status = DealStatus.NEW;
    }

    public String getTitle() {
        return title;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getProductId() {
        return productId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public DealStatus getStatus() {
        return status;
    }

    public void setStatus(DealStatus status) {
        this.status = Validation.requireNotNull(status, "status");
    }

    @Override
    public String toString() {
        return "Сделка #" + getId() + ": " + title + ", клиент " + clientId + ", продукт " + productId
                + ", сумма " + amount + ", статус " + status;
    }
}
