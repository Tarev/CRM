package com.example.crm.service;

import com.example.crm.model.Client;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService extends AbstractService<Client> {

    public ClientService(EntityRepository<Client> repository) {
        super(repository);
    }

    /** Ищет клиентов, в имени которых есть указанный текст (без учета регистра). */
    public List<Client> findByName(String part) {
        List<Client> result = new ArrayList<>();
        if (part == null) {
            return result;
        }
        String search = part.trim().toLowerCase();
        for (Client client : findAll()) {
            if (client.getName().toLowerCase().contains(search)) {
                result.add(client);
            }
        }
        return result;
    }

    public List<Client> findByCompanyId(Long companyId) {
        List<Client> result = new ArrayList<>();
        for (Client client : findAll()) {
            if (companyId != null && companyId.equals(client.getCompanyId())) {
                result.add(client);
            }
        }
        return result;
    }
}
