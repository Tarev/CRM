package com.example.crm.dto;

import com.example.crm.model.DealStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DealRequest {

    @NotBlank(message = "Номер сделки не должен быть пустым")
    @Size(max = 50, message = "Номер сделки не длиннее 50 символов")
    private String dealNumber;

    @NotBlank(message = "Название сделки не должно быть пустым")
    @Size(max = 255, message = "Название сделки не длиннее 255 символов")
    private String title;

    @NotNull(message = "Сумма обязательна")
    @PositiveOrZero(message = "Сумма не может быть отрицательной")
    @Digits(integer = 10, fraction = 2, message = "Сумма: до 10 цифр до запятой и 2 после")
    private BigDecimal amount;

    /** Если не указан, будет NEW. */
    private DealStatus status;

    @NotNull(message = "Нужно указать id клиента")
    private Long clientId;

    @NotNull(message = "Нужно указать id продукта")
    private Long productId;
}
