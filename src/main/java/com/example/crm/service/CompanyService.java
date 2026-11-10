package com.example.crm.service;

import com.example.crm.model.Company;
import com.example.crm.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService extends AbstractService<Company> {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        super(companyRepository);
        this.companyRepository = companyRepository;
    }

    /** Если город не указан, возвращает все компании. */
    public List<Company> findByCity(String city) {
        if (city == null || city.isBlank()) {
            return findAll();
        }
        return companyRepository.findByCityIgnoreCase(city.trim());
    }

    /** Создает компанию (id из запроса игнорируется). */
    public Company create(Company company) {
        company.setId(null);
        return save(company);
    }

    /** Обновляет компанию. Возвращает empty, если компании с таким id нет. */
    public Optional<Company> update(Long id, Company company) {
        if (!existsById(id)) {
            return Optional.empty();
        }
        company.setId(id);
        return Optional.of(save(company));
    }
}
