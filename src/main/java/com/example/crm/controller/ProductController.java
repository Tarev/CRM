package com.example.crm.controller;

import com.example.crm.model.Product;
import com.example.crm.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAll(@RequestParam(required = false) String name,
                                @RequestParam(required = false) BigDecimal maxPrice) {
        return productService.search(name, maxPrice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Optional<Product> found = productService.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found.get());
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product body) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Optional<Product> updated = productService.update(id, body);
        if (updated.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!productService.deleteById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
