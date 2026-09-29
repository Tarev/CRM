package com.example.crm.service;

import com.example.crm.model.Product;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
public class ProductService extends AbstractService<Product> {

    public ProductService(EntityRepository<Product> repository) {
        super(repository);
    }

    public List<Product> findByName(String part) {
        List<Product> result = new ArrayList<>();
        if (part == null) {
            return result;
        }
        String search = part.trim().toLowerCase();
        for (Product product : findAll()) {
            if (product.getName().toLowerCase().contains(search)) {
                result.add(product);
            }
        }
        return result;
    }

    /** Продукты, цена которых не выше указанной. */
    public List<Product> findWithPriceUpTo(BigDecimal maxPrice) {
        List<Product> result = new ArrayList<>();
        for (Product product : findAll()) {
            if (product.getPrice().compareTo(maxPrice) <= 0) {
                result.add(product);
            }
        }
        return result;
    }
}
