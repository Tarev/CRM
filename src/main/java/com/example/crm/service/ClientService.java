package com.example.crm.service;

import com.example.crm.dto.ClientRequest;
import com.example.crm.exception.ResourceInUseException;
import com.example.crm.model.Client;
import com.example.crm.model.ClientType;
import com.example.crm.model.Company;
import com.example.crm.repository.ClientRepository;
import com.example.crm.repository.DealRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService extends AbstractService<Client> {

    private final DealRepository dealRepository;
    private final ClientRepository clientRepository;
    private final CompanyService companyService;

    public ClientService(ClientRepository clientRepository, CompanyService companyService, DealRepository dealRepository) {
        super(clientRepository, "Клиент");
        this.clientRepository = clientRepository;
        this.dealRepository = dealRepository;
        this.companyService = companyService;
    }

    /** Поиск по части имени, типу и компании. Любой параметр можно не указывать (null). */
    public List<Client> search(String name, ClientType type, Long companyId) {
        String searchName = name == null ? "" : name.trim();
        return clientRepository.search(searchName, type, companyId);
    }

    /** Создает клиента. */
    public Client create(ClientRequest request) {
        return fillAndSave(new Client(), request);
    }

    /** Обновляет клиента. */
    public Client update(Long id, ClientRequest request) {
        Client client = getById(id);
        return fillAndSave(client, request);
    }

    private Client fillAndSave(Client client, ClientRequest request) {
        Company company = null;
        if (request.getCompanyId() != null) {
            company = companyService.requireReference(request.getCompanyId());
        }
        requireUnique(clientRepository.findByEmail(request.getEmail()), client.getId(), "email");
        client.setEmail(request.getEmail());
        client.setName(request.getName());
        client.setPhone(request.getPhone());
        if (request.getClientType() != null) {
            client.setClientType(request.getClientType());
        }
        client.setCompany(company);
        return save(client);
    }

    @Override
    protected void checkCanDelete(Long id) {
        if (dealRepository.existsByClientId(id)) {
            throw new ResourceInUseException("клиента", "сделки");
        }
    }
}
