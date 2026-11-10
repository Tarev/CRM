package com.example.crm.service;

import com.example.crm.model.Product;
import com.example.crm.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService extends AbstractService<Product> {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        super(productRepository);
        this.productRepository = productRepository;
    }

    /** Поиск по части названия и максимальной цене. Любой параметр можно не указывать (null). */
    public List<Product> search(String name, BigDecimal maxPrice) {
        String searchName = name == null ? "" : name.trim();
        return productRepository.search(searchName, maxPrice);
    }

    /** Создает продукт (id из запроса игнорируется). */
    public Product create(Product product) {
        product.setId(null);
        return save(product);
    }

    /** Обновляет продукт. Возвращает empty, если продукта с таким id нет. */
    public Optional<Product> update(Long id, Product product) {
        if (!existsById(id)) {
            return Optional.empty();
        }
        product.setId(id);
        return Optional.of(save(product));
    }
}
