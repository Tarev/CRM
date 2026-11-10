package com.example.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoteRequest {

    @NotNull(message = "Нужно указать id сделки")
    private Long dealId;

    @NotBlank(message = "Текст заметки не должен быть пустым")
    private String text;
}
