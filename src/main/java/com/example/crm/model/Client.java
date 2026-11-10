package com.example.crm.model;

import com.example.crm.util.Validation;

public class Client extends BaseEntity {

    private final String name;
    private final String email;
    private final String phone;
    /** id компании клиента, может быть null (частное лицо). */
    private final Long companyId;

    public Client(String name, String email, String phone, Long companyId) {
        this.name = Validation.requireNotBlank(name, "name");
        this.email = Validation.requireNotBlank(email, "email");
        this.phone = Validation.requireNotBlank(phone, "phone");
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public String toString() {
        String company = companyId == null ? "нет" : String.valueOf(companyId);
        return "Клиент #" + getId() + ": " + name + ", " + email + ", " + phone + ", компания: " + company;
    }
}
