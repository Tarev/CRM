package com.example.crm.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.util.Map;

/** Преобразует ограничения PostgreSQL в ошибки CRM, не раскрывая SQL клиенту. */
public final class DatabaseExceptionTranslator {
    private static final Map<String, String> UNIQUE_FIELDS = Map.of(
            "companies_name_key", "company.name", "clients_email_key", "client.email",
            "contacts_email_key", "contact.email", "products_article_key", "product.article",
            "deals_deal_number_key", "deal.dealNumber", "users_login_key", "user.login",
            "users_client_id_key", "user.clientId");
    private static final Map<String, String> REFERENCES = Map.of(
            "clients_company_id_fkey", "компания клиента",
            "contacts_company_id_fkey", "компания контакта",
            "deals_client_id_fkey", "клиент сделки",
            "deals_product_id_fkey", "продукт сделки",
            "users_client_id_fkey", "клиент пользователя",
            "tasks_deal_id_fkey", "сделка задачи",
            "tasks_assignee_id_fkey", "исполнитель задачи",
            "notes_deal_id_fkey", "сделка заметки");

    private DatabaseExceptionTranslator() { }

    public static CrmException translate(DataIntegrityViolationException exception, boolean deleting) {
        String constraint = null;
        String state = null;
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                constraint = violation.getConstraintName();
            }
            if (cause instanceof SQLException sql) {
                state = sql.getSQLState();
            }
        }
        CrmException result;
        if ("23505".equals(state)) {
            result = new DuplicateResourceException(UNIQUE_FIELDS.getOrDefault(constraint == null ? "" : constraint,
                    "уникального значения"));
        } else if ("23503".equals(state)) {
            String reference = REFERENCES.getOrDefault(constraint == null ? "" : constraint, "связанный объект");
            if (deleting) {
                result = new ResourceInUseException("объект", "записи со связью: " + reference);
            } else {
                result = new BusinessRuleViolationException("Недоступен связанный объект: " + reference + ".",
                        "Обновите список связанных объектов и укажите существующий ID.");
            }
        } else if ("23514".equals(state) || "23502".equals(state) || "22001".equals(state)
                || "22003".equals(state)) {
            result = new BusinessRuleViolationException("Значения полей не соответствуют ограничениям базы данных.",
                    "Проверьте обязательные поля, длину строк и допустимые числовые значения.");
        } else {
            result = new DataConflictException();
        }
        result.initCause(exception);
        return result;
    }
}
