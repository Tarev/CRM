package com.example.crm.repository;

import com.example.crm.model.Client;
import com.example.crm.model.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /** Фильтры необязательные: если параметр null, он не учитывается. Имя передается "" вместо null. */
    @Query("select c from Client c where lower(c.name) like lower(concat('%', :name, '%'))"
            + " and (:type is null or c.clientType = :type)"
            + " and (:companyId is null or c.company.id = :companyId)")
    List<Client> search(@Param("name") String name,
                        @Param("type") ClientType type,
                        @Param("companyId") Long companyId);
}
