package com.example.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * Результат многопоточной обработки сделок.
 */
@Getter
@AllArgsConstructor
public class ProcessResponse {

    private int processedDeals;
    private Set<String> threads;
}
