package com.example.crm.dto;

import com.example.crm.model.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Логин не должен быть пустым")
    @Size(max = 50, message = "Логин не длиннее 50 символов")
    private String login;

    @NotBlank(message = "ФИО не должно быть пустым")
    @Size(max = 255, message = "ФИО не длиннее 255 символов")
    private String fullName;

    @NotNull(message = "Нужно указать роль (ADMIN, MANAGER, CUSTOMER)")
    private UserRole role;

    /** Только для роли CUSTOMER: id клиента, к которому привязан аккаунт. */
    private Long clientId;
}
