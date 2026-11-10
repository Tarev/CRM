package com.example.crm.service;

import com.example.crm.dto.ContactRequest;
import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import com.example.crm.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService extends AbstractService<Contact> {

    private final ContactRepository contactRepository;
    private final CompanyService companyService;

    public ContactService(ContactRepository contactRepository, CompanyService companyService) {
        super(contactRepository);
        this.contactRepository = contactRepository;
        this.companyService = companyService;
    }

    /** Если id компании не указан, возвращает все контакты. */
    public List<Contact> findByCompanyId(Long companyId) {
        if (companyId == null) {
            return findAll();
        }
        return contactRepository.findByCompanyId(companyId);
    }

    /** Создает контакт. Возвращает empty, если компания не найдена. */
    public Optional<Contact> create(ContactRequest request) {
        return fillAndSave(new Contact(), request);
    }

    /** Обновляет контакт. Возвращает empty, если контакт или компания не найдены. */
    public Optional<Contact> update(Long id, ContactRequest request) {
        Optional<Contact> contact = findById(id);
        if (contact.isEmpty()) {
            return Optional.empty();
        }
        return fillAndSave(contact.get(), request);
    }

    private Optional<Contact> fillAndSave(Contact contact, ContactRequest request) {
        Optional<Company> company = companyService.findById(request.getCompanyId());
        if (company.isEmpty()) {
            return Optional.empty();
        }
        contact.setEmail(request.getEmail());
        contact.setFullName(request.getFullName());
        contact.setPosition(request.getPosition());
        contact.setCompany(company.get());
        return Optional.of(save(contact));
    }
}
