package com.example.crm.dto;

import com.example.crm.model.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientRequest {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Неверный формат email")
    @Size(max = 255, message = "Email не длиннее 255 символов")
    private String email;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(max = 255, message = "Имя не длиннее 255 символов")
    private String name;

    @Pattern(regexp = "\\+7\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}", message = "Телефон должен быть в формате +7(XXX)XXX-XX-XX")
    private String phone;

    /** Если не указан, будет INDIVIDUAL. */
    private ClientType clientType;

    /** Необязательно: id компании клиента. */
    private Long companyId;
}
