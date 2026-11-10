package com.example.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactRequest {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Неверный формат email")
    @Size(max = 255, message = "Email не длиннее 255 символов")
    private String email;

    @NotBlank(message = "ФИО не должно быть пустым")
    @Size(max = 255, message = "ФИО не длиннее 255 символов")
    private String fullName;

    @Size(max = 100, message = "Должность не длиннее 100 символов")
    private String position;

    @NotNull(message = "Нужно указать id компании")
    private Long companyId;
}
