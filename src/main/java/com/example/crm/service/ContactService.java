package com.example.crm.service;

import com.example.crm.dto.ContactRequest;
import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import com.example.crm.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService extends AbstractService<Contact> {

    private final ContactRepository contactRepository;
    private final CompanyService companyService;

    public ContactService(ContactRepository contactRepository, CompanyService companyService) {
        super(contactRepository, "Контакт");
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

    /** Создает контакт. */
    public Contact create(ContactRequest request) {
        return fillAndSave(new Contact(), request);
    }

    /** Обновляет контакт. */
    public Contact update(Long id, ContactRequest request) {
        Contact contact = getById(id);
        return fillAndSave(contact, request);
    }

    private Contact fillAndSave(Contact contact, ContactRequest request) {
        Company company = companyService.requireReference(request.getCompanyId());
        requireUnique(contactRepository.findByEmail(request.getEmail()), contact.getId(), "email");
        contact.setEmail(request.getEmail());
        contact.setFullName(request.getFullName());
        contact.setPosition(request.getPosition());
        contact.setCompany(company);
        return save(contact);
    }
}
