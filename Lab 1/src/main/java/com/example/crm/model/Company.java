package com.example.crm.model;

import com.example.crm.util.Validation;

public class Company extends BaseEntity {

    private final String name;
    private final String city;

    public Company(String name, String city) {
        this.name = Validation.requireNotBlank(name, "name");
        this.city = Validation.requireNotBlank(city, "city");
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "Компания #" + getId() + ": " + name + ", город " + city;
    }
}
