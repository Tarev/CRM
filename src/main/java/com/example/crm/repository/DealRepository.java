package com.example.crm.repository;

import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByStatus(DealStatus status);

    /** Фильтры необязательные: если параметр null, он не учитывается. */
    @Query("select d from Deal d where (:status is null or d.status = :status)"
            + " and (:clientId is null or d.client.id = :clientId)"
            + " and (:minAmount is null or d.amount >= :minAmount)"
            + " and (:maxAmount is null or d.amount <= :maxAmount)")
    List<Deal> search(@Param("status") DealStatus status,
                      @Param("clientId") Long clientId,
                      @Param("minAmount") BigDecimal minAmount,
                      @Param("maxAmount") BigDecimal maxAmount);
}
