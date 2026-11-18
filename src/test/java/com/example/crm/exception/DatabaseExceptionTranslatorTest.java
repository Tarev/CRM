package com.example.crm.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseExceptionTranslatorTest {
    private DataIntegrityViolationException violation(String state, String constraint) {
        SQLException sql = new SQLException("private SQL details", state);
        return new DataIntegrityViolationException("private details",
                new ConstraintViolationException("private constraint", sql, constraint));
    }

    @ParameterizedTest
    @CsvSource({"companies_name_key,company.name", "clients_email_key,client.email",
            "contacts_email_key,contact.email", "products_article_key,product.article",
            "deals_deal_number_key,deal.dealNumber", "users_login_key,user.login", "users_client_id_key,user.clientId"})
    void namesUniqueFieldAndPreservesCause(String constraint, String field) {
        DataIntegrityViolationException original = violation("23505", constraint);
        CrmException result = DatabaseExceptionTranslator.translate(original, false);
        assertInstanceOf(DuplicateResourceException.class, result);
        assertTrue(result.getMessage().contains(field));
        assertFalse(result.getMessage().contains("private"));
        assertSame(original, result.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {"clients_company_id_fkey", "contacts_company_id_fkey", "deals_client_id_fkey",
            "deals_product_id_fkey", "users_client_id_fkey", "tasks_deal_id_fkey", "tasks_assignee_id_fkey",
            "notes_deal_id_fkey"})
    void distinguishesDeletedDependencyFromInvalidReference(String constraint) {
        assertInstanceOf(ResourceInUseException.class,
                DatabaseExceptionTranslator.translate(violation("23503", constraint), true));
        CrmException reference = DatabaseExceptionTranslator.translate(violation("23503", constraint), false);
        assertInstanceOf(BusinessRuleViolationException.class, reference);
        assertTrue(reference.getSuggestion().contains("ID"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"23514", "23502", "22001", "22003"})
    void invalidValuesHaveCorrectionAdvice(String state) {
        CrmException result = DatabaseExceptionTranslator.translate(violation(state, null), false);
        assertInstanceOf(BusinessRuleViolationException.class, result);
        assertTrue(result.getSuggestion().contains("поля"));
    }

    @Test
    void unknownConstraintAndMissingCauseAreSafe() {
        assertInstanceOf(DuplicateResourceException.class,
                DatabaseExceptionTranslator.translate(violation("23505", null), false));
        assertInstanceOf(DataConflictException.class, DatabaseExceptionTranslator.translate(
                new DataIntegrityViolationException("private details"), false));
    }
}
