package com.example.crm.service;

import com.example.crm.exception.ResourceInUseException;
import com.example.crm.model.Product;
import com.example.crm.repository.DealRepository;
import com.example.crm.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService extends AbstractService<Product> {

    private final DealRepository dealRepository;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository, DealRepository dealRepository) {
        super(productRepository, "Продукт");
        this.productRepository = productRepository;
        this.dealRepository = dealRepository;
    }

    /** Поиск по части названия и максимальной цене. Любой параметр можно не указывать (null). */
    public List<Product> search(String name, BigDecimal maxPrice) {
        String searchName = name == null ? "" : name.trim();
        return productRepository.search(searchName, maxPrice);
    }

    /** Создает продукт (id из запроса игнорируется). */
    public Product create(Product product) {
        requireUnique(productRepository.findByArticle(product.getArticle()), null, "article");
        product.setId(null);
        return save(product);
    }

    /** Обновляет продукт. */
    public Product update(Long id, Product product) {
        requireExists(id);
        requireUnique(productRepository.findByArticle(product.getArticle()), id, "article");
        product.setId(id);
        return save(product);
    }

    @Override
    protected void checkCanDelete(Long id) {
        if (dealRepository.existsByProductId(id)) {
            throw new ResourceInUseException("продукт", "сделки");
        }
    }
}
