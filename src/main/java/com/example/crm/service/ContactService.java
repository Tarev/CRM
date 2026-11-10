package com.example.crm.service;

import com.example.crm.model.Contact;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactService extends AbstractService<Contact> {

    public ContactService(EntityRepository<Contact> repository) {
        super(repository);
    }

    public List<Contact> findByCompanyId(Long companyId) {
        List<Contact> result = new ArrayList<>();
        for (Contact contact : findAll()) {
            if (companyId != null && companyId.equals(contact.getCompanyId())) {
                result.add(contact);
            }
        }
        return result;
    }
}
