package com.example.crm.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/** Единый ответ REST API об ошибке. Значения входных полей и SQL не публикуются. */
public record ApiError(OffsetDateTime timestamp, int status, String code, String message,
                       String path, String suggestion, Map<String, List<String>> fieldErrors) {
}
