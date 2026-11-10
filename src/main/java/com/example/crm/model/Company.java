package com.example.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    @NotBlank(message = "Название компании не должно быть пустым")
    @Size(max = 255, message = "Название компании не длиннее 255 символов")
    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @NotBlank(message = "Город не должен быть пустым")
    @Size(max = 100, message = "Город не длиннее 100 символов")
    @Column(name = "city", nullable = false, length = 100)
    private String city;
}
