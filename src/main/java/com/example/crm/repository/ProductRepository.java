package com.example.crm.repository;

import com.example.crm.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Фильтры необязательные: если параметр null, он не учитывается. Название передается "" вместо null. */
    @Query("select p from Product p where lower(p.name) like lower(concat('%', :name, '%'))"
            + " and (:maxPrice is null or p.price <= :maxPrice)")
    List<Product> search(@Param("name") String name, @Param("maxPrice") BigDecimal maxPrice);
}
