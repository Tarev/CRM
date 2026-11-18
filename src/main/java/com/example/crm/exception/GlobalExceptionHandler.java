package com.example.crm.exception;

import com.example.crm.dto.ApiError;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Каждый ожидаемый тип ошибки имеет собственный обработчик и рекомендацию. */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> notFound(EntityNotFoundException exception, WebRequest request) {
        return domain(exception, HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", request);
    }

    @ExceptionHandler(InvalidReferenceException.class)
    public ResponseEntity<Object> invalidReference(InvalidReferenceException exception, WebRequest request) {
        return domain(exception, HttpStatus.BAD_REQUEST, "INVALID_REFERENCE", request);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<Object> businessRule(BusinessRuleViolationException exception, WebRequest request) {
        return domain(exception, HttpStatus.BAD_REQUEST, "BUSINESS_RULE_VIOLATION", request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> duplicate(DuplicateResourceException exception, WebRequest request) {
        return domain(exception, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", request);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<Object> inUse(ResourceInUseException exception, WebRequest request) {
        return domain(exception, HttpStatus.CONFLICT, "RESOURCE_IN_USE", request);
    }

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<Object> conflict(DataConflictException exception, WebRequest request) {
        return domain(exception, HttpStatus.CONFLICT, "DATA_CONFLICT", request);
    }

    @ExceptionHandler(DealProcessingException.class)
    public ResponseEntity<Object> processing(DealProcessingException exception, WebRequest request) {
        log.error("Не удалось обработать сделки", exception);
        return domain(exception, HttpStatus.INTERNAL_SERVER_ERROR, "DEAL_PROCESSING_FAILED", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> integrity(DataIntegrityViolationException exception, WebRequest request) {
        boolean deleting = request instanceof ServletWebRequest servlet
                && "DELETE".equals(servlet.getRequest().getMethod());
        CrmException translated = DatabaseExceptionTranslator.translate(exception, deleting);
        if (translated instanceof DuplicateResourceException duplicate) {
            return duplicate(duplicate, request);
        }
        if (translated instanceof ResourceInUseException inUse) {
            return inUse(inUse, request);
        }
        if (translated instanceof BusinessRuleViolationException businessRule) {
            return businessRule(businessRule, request);
        }
        return conflict((DataConflictException) translated, request);
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Object> databaseUnavailable(DataAccessResourceFailureException exception, WebRequest request) {
        log.error("База данных недоступна", exception);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "DATABASE_UNAVAILABLE", "База данных временно недоступна.",
                "Повторите запрос позже. Проверьте, запущен ли PostgreSQL.", Map.of(), new HttpHeaders(), request);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> databaseFailure(DataAccessException exception, WebRequest request) {
        log.error("Ошибка доступа к данным", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "Не удалось выполнить операцию с данными.",
                "Обратитесь к администратору.", Map.of(), new HttpHeaders(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, List<String>> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(field ->
                fields.computeIfAbsent(field.getField(), key -> new ArrayList<>()).add(field.getDefaultMessage()));
        return error(status, "VALIDATION_ERROR", "Поля запроса не прошли проверку.",
                "Исправьте поля, перечисленные в fieldErrors.", fields, headers, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> validation(ConstraintViolationException exception, WebRequest request) {
        Map<String, List<String>> fields = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation -> fields
                .computeIfAbsent(violation.getPropertyPath().toString(), key -> new ArrayList<>())
                .add(violation.getMessage()));
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Значения не прошли проверку.",
                "Исправьте поля, перечисленные в fieldErrors.", fields, new HttpHeaders(), request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return error(status, "INVALID_JSON", "Тело запроса отсутствует или содержит некорректные данные JSON.",
                "Проверьте синтаксис JSON, типы полей, значения перечислений и даты в формате YYYY-MM-DD.",
                Map.of(), headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String name = exception instanceof MethodArgumentTypeMismatchException argument
                ? argument.getName() : "параметр";
        Class<?> type = exception.getRequiredType();
        String suggestion = "Проверьте тип и формат параметра " + name + ".";
        if (type != null && type.isEnum()) {
            suggestion = "Допустимые значения: " + Arrays.stream(type.getEnumConstants())
                    .map(Object::toString).collect(Collectors.joining(", ")) + ".";
        }
        return error(status, "INVALID_PARAMETER", "Некорректный параметр " + name + ".",
                suggestion, Map.of(), headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return error(status, "MISSING_PARAMETER", "Не указан обязательный параметр " + exception.getParameterName() + ".",
                "Добавьте параметр " + exception.getParameterName() + " в строку запроса.", Map.of(), headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return error(status, "HTTP_" + status.value(), "Запрос не может быть выполнен (HTTP " + status.value() + ").",
                "Проверьте адрес, HTTP-метод и Content-Type запроса.", Map.of(), headers, request);
    }

    /** Резервный обработчик неожиданных ошибок; известные ошибки обрабатываются выше. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> unexpected(Exception exception, WebRequest request) {
        log.error("Непредвиденная ошибка запроса", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Внутренняя ошибка сервера.",
                "Обратитесь к администратору, указав время и адрес запроса.", Map.of(), new HttpHeaders(), request);
    }

    private ResponseEntity<Object> domain(CrmException exception, HttpStatus status, String code, WebRequest request) {
        return error(status, code, exception.getMessage(), exception.getSuggestion(), Map.of(), new HttpHeaders(), request);
    }

    private ResponseEntity<Object> error(HttpStatusCode status, String code, String message, String suggestion,
            Map<String, List<String>> fields, HttpHeaders headers, WebRequest request) {
        String path = request instanceof ServletWebRequest servlet ? servlet.getRequest().getRequestURI() : "";
        ApiError body = new ApiError(OffsetDateTime.now(), status.value(), code, message, path, suggestion, fields);
        return new ResponseEntity<>(body, headers, status);
    }
}
