package com.example.crm.service;

import com.example.crm.dto.ClientRequest;
import com.example.crm.model.Client;
import com.example.crm.model.ClientType;
import com.example.crm.model.Company;
import com.example.crm.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService extends AbstractService<Client> {

    private final ClientRepository clientRepository;
    private final CompanyService companyService;

    public ClientService(ClientRepository clientRepository, CompanyService companyService) {
        super(clientRepository);
        this.clientRepository = clientRepository;
        this.companyService = companyService;
    }

    /** Поиск по части имени, типу и компании. Любой параметр можно не указывать (null). */
    public List<Client> search(String name, ClientType type, Long companyId) {
        String searchName = name == null ? "" : name.trim();
        return clientRepository.search(searchName, type, companyId);
    }

    /** Создает клиента. Возвращает empty, если указанная компания не найдена. */
    public Optional<Client> create(ClientRequest request) {
        return fillAndSave(new Client(), request);
    }

    /** Обновляет клиента. Возвращает empty, если клиент или компания не найдены. */
    public Optional<Client> update(Long id, ClientRequest request) {
        Optional<Client> client = findById(id);
        if (client.isEmpty()) {
            return Optional.empty();
        }
        return fillAndSave(client.get(), request);
    }

    private Optional<Client> fillAndSave(Client client, ClientRequest request) {
        Company company = null;
        if (request.getCompanyId() != null) {
            Optional<Company> found = companyService.findById(request.getCompanyId());
            if (found.isEmpty()) {
                return Optional.empty();
            }
            company = found.get();
        }
        client.setEmail(request.getEmail());
        client.setName(request.getName());
        client.setPhone(request.getPhone());
        if (request.getClientType() != null) {
            client.setClientType(request.getClientType());
        }
        client.setCompany(company);
        return Optional.of(save(client));
    }
}
