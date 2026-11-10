package com.example.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @NotBlank(message = "Артикул не должен быть пустым")
    @Size(max = 50, message = "Артикул не длиннее 50 символов")
    @Column(name = "article", nullable = false, unique = true, length = 50)
    private String article;

    @NotBlank(message = "Название продукта не должно быть пустым")
    @Size(max = 255, message = "Название продукта не длиннее 255 символов")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotNull(message = "Цена обязательна")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    @Digits(integer = 8, fraction = 2, message = "Цена: до 8 цифр до запятой и 2 после")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
