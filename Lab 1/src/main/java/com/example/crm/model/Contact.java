package com.example.crm.model;

import com.example.crm.util.Validation;

/**
 * Контактное лицо компании.
 */
public class Contact extends BaseEntity {

    private final String fullName;
    private final String position;
    private final String phone;
    private final Long companyId;

    public Contact(String fullName, String position, String phone, Long companyId) {
        this.fullName = Validation.requireNotBlank(fullName, "fullName");
        this.position = Validation.requireNotBlank(position, "position");
        this.phone = Validation.requireNotBlank(phone, "phone");
        this.companyId = Validation.requireNotNull(companyId, "companyId");
    }

    public String getFullName() {
        return fullName;
    }

    public String getPosition() {
        return position;
    }

    public String getPhone() {
        return phone;
    }

    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public String toString() {
        return "Контакт #" + getId() + ": " + fullName + ", " + position + ", " + phone + ", компания: " + companyId;
    }
}
