package com.example.crm.repository;

import com.example.crm.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByEmail(String value);

    List<Contact> findByCompanyId(Long companyId);
}
