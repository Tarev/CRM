package com.example.crm.service;

import com.example.crm.model.Company;
import com.example.crm.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService extends AbstractService<Company> {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        super(companyRepository, "Компания");
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
        requireUnique(companyRepository.findByName(company.getName()), null, "name");
        company.setId(null);
        return save(company);
    }

    /** Обновляет компанию. */
    public Company update(Long id, Company company) {
        requireExists(id);
        requireUnique(companyRepository.findByName(company.getName()), id, "name");
        company.setId(id);
        return save(company);
    }
}
