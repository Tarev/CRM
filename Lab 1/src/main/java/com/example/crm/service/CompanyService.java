package com.example.crm.service;

import com.example.crm.model.Company;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService extends AbstractService<Company> {

    public CompanyService(EntityRepository<Company> repository) {
        super(repository);
    }

    public List<Company> findByCity(String city) {
        List<Company> result = new ArrayList<>();
        if (city == null) {
            return result;
        }
        for (Company company : findAll()) {
            if (company.getCity().equalsIgnoreCase(city.trim())) {
                result.add(company);
            }
        }
        return result;
    }
}
