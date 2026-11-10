package com.example.crm.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Название задачи не должно быть пустым")
    @Size(max = 255, message = "Название задачи не длиннее 255 символов")
    private String title;

    @NotNull(message = "Срок исполнения обязателен")
    @FutureOrPresent(message = "Срок исполнения не может быть в прошлом")
    private LocalDate deadline;

    /** Если не указано, задача считается невыполненной. */
    private Boolean done;

    /** Необязательно: id сделки (общая задача без сделки). */
    private Long dealId;

    @NotNull(message = "Нужно указать id исполнителя")
    private Long assigneeId;
}
