package com.example.crm.model;

import com.example.crm.util.Validation;

import java.math.BigDecimal;

public class Product extends BaseEntity {

    private final String name;
    private final BigDecimal price;

    public Product(String name, BigDecimal price) {
        this.name = Validation.requireNotBlank(name, "name");
        this.price = Validation.requireNotNegative(price, "price");
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Продукт #" + getId() + ": " + name + ", цена " + price;
    }
}
