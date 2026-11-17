package com.example.crm.controller;

import com.example.crm.MockRepositoriesTest;
import com.example.crm.model.Client;
import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import com.example.crm.model.Product;
import com.example.crm.model.user.CustomerUser;
import com.example.crm.model.user.ManagerUser;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Контракт ошибок проверяется через реальный Spring MVC и сервисы, репозитории — Mockito. */
class ErrorHandlingTest extends MockRepositoriesTest {
    @Autowired
    private MockMvc mvc;

    @ParameterizedTest
    @ValueSource(strings = {"companies", "clients", "contacts", "products", "deals", "tasks", "notes", "users"})
    void missingObjectHasDetailed404(String resource) throws Exception {
        mvc.perform(get("/api/" + resource + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("999")))
                .andExpect(jsonPath("$.path").value("/api/" + resource + "/999"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.suggestion").value(not(emptyOrNullString())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"companies", "clients", "contacts", "products", "deals", "tasks", "notes", "users"})
    void deletingMissingObjectHasDetailed404(String resource) throws Exception {
        mvc.perform(delete("/api/" + resource + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    @Test
    void invalidFieldsIncludeAllMessages() throws Exception {
        mvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"wrong\",\"name\":\"\",\"phone\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.email[0]").value("Неверный формат email"))
                .andExpect(jsonPath("$.fieldErrors.name[0]").exists())
                .andExpect(jsonPath("$.fieldErrors.phone[0]").exists());
        verify(clientRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "", "{\"amount\":\"abc\"}", "{\"status\":\"WRONG\"}"})
    void malformedBodyHasHelpful400(String json) throws Exception {
        mvc.perform(post("/api/deals").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_JSON"));
    }

    @Test
    void invalidEnumListsAllowedValues() throws Exception {
        mvc.perform(get("/api/deals?status=WRONG"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"))
                .andExpect(jsonPath("$.suggestion").value(containsString("IN_PROGRESS")));
    }

    @Test
    void invalidIdHasParameterName() throws Exception {
        mvc.perform(get("/api/clients/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("id")));
    }

    @Test
    void missingRequiredParameterHasHelpful400() throws Exception {
        mvc.perform(get("/api/deals/total"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"))
                .andExpect(jsonPath("$.suggestion").value(containsString("status")));
    }

    @Test
    void invalidReferenceNamesMissingEntity() throws Exception {
        mvc.perform(post("/api/clients").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@mail.ru\",\"name\":\"A\",\"companyId\":77}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REFERENCE"))
                .andExpect(jsonPath("$.message").value(containsString("Компания с id=77")));
        verify(clientRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({
            "companies,'{\"name\":\"A\",\"city\":\"M\"}',name",
            "clients,'{\"email\":\"a@mail.ru\",\"name\":\"A\"}',email",
            "contacts,'{\"email\":\"a@mail.ru\",\"fullName\":\"A\",\"companyId\":1}',email",
            "products,'{\"article\":\"A\",\"name\":\"A\",\"price\":1}',article",
            "deals,'{\"dealNumber\":\"D\",\"title\":\"A\",\"amount\":1,\"clientId\":1,\"productId\":1}',dealNumber",
            "users,'{\"login\":\"a\",\"fullName\":\"A\",\"role\":\"MANAGER\"}',login"
    })
    void duplicateUniqueFieldHas409(String resource, String body, String field) throws Exception {
        switch (resource) {
            case "companies" -> when(companyRepository.findByName("A")).thenReturn(Optional.of(new Company()));
            case "clients" -> when(clientRepository.findByEmail("a@mail.ru")).thenReturn(Optional.of(new Client()));
            case "contacts" -> {
                when(companyRepository.findById(1L)).thenReturn(Optional.of(new Company()));
                when(contactRepository.findByEmail("a@mail.ru")).thenReturn(Optional.of(new Contact()));
            }
            case "products" -> when(productRepository.findByArticle("A")).thenReturn(Optional.of(new Product()));
            case "deals" -> {
                when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
                when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
                when(dealRepository.findByDealNumber("D")).thenReturn(Optional.of(new Deal()));
            }
            case "users" -> when(userRepository.findByLogin("a")).thenReturn(Optional.of(new ManagerUser()));
            default -> throw new AssertionError(resource);
        }
        mvc.perform(post("/api/" + resource).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.message").value(containsString(field)));
        verify(companyRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
        verify(contactRepository, never()).save(any());
        verify(productRepository, never()).save(any());
        verify(dealRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void clientAccountCannotBeLinkedTwice() throws Exception {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(new Client()));
        when(userRepository.findCustomerByClientId(1L)).thenReturn(Optional.of(new CustomerUser()));
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"a\",\"fullName\":\"A\",\"role\":\"CUSTOMER\",\"clientId\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("clientId")));
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"clients", "products", "users"})
    void deletingUsedObjectHas409(String resource) throws Exception {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(dealRepository.existsByClientId(1L)).thenReturn(true);
        when(dealRepository.existsByProductId(1L)).thenReturn(true);
        when(taskRepository.existsByAssigneeId(1L)).thenReturn(true);
        mvc.perform(delete("/api/" + resource + "/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_IN_USE"))
                .andExpect(jsonPath("$.suggestion").value(containsString("переназначьте")));
        verify(clientRepository, never()).deleteById(any());
        verify(productRepository, never()).deleteById(any());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void databaseRaceOnUniqueValueIsAlsoHandled() throws Exception {
        SQLException sql = new SQLException("secret SQL and values", "23505");
        ConstraintViolationException constraint = new ConstraintViolationException("secret", sql, "companies_name_key");
        when(companyRepository.save(any())).thenThrow(new DataIntegrityViolationException("secret", constraint));
        mvc.perform(post("/api/companies").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"city\":\"M\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.message").value(containsString("company.name")))
                .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void roleChangeHasBusinessRuleMessage() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new ManagerUser()));
        mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"a\",\"fullName\":\"A\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                .andExpect(jsonPath("$.suggestion").value(containsString("роль")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void invalidAmountRangeHas400() throws Exception {
        mvc.perform(get("/api/deals?minAmount=100&maxAmount=10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.suggestion").value(containsString("minAmount")));
        verify(dealRepository, never()).search(any(), any(), any(), any());
    }

    @Test
    void databaseUnavailableHas503WithoutInternalDetails() throws Exception {
        when(companyRepository.findAll()).thenThrow(new DataAccessResourceFailureException("secret connection"));
        mvc.perform(get("/api/companies"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("DATABASE_UNAVAILABLE"))
                .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void unexpectedErrorHas500WithoutInternalDetails() throws Exception {
        when(companyRepository.findAll()).thenThrow(new IllegalStateException("secret stack trace"));
        mvc.perform(get("/api/companies"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void frameworkErrorPreservesStatusAndHeaders() throws Exception {
        mvc.perform(patch("/api/companies/1"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().exists("Allow"))
                .andExpect(jsonPath("$.code").value("HTTP_405"));
    }

    @Test
    void workerFailureIsReportedAndNotSaved() throws Exception {
        Deal broken = mock(Deal.class);
        when(broken.getStatus()).thenThrow(new IllegalStateException("secret worker error"));
        when(dealRepository.findByStatus(DealStatus.NEW)).thenReturn(List.of(broken));
        mvc.perform(post("/api/deals/process-new"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("DEAL_PROCESSING_FAILED"))
                .andExpect(content().string(not(containsString("secret"))));
        verify(dealRepository, never()).saveAll(any());
    }

    @Test
    void deleteRaceIsTranslatedToResourceInUse() throws Exception {
        when(clientRepository.existsById(1L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("private", new ConstraintViolationException("private",
                new SQLException("private SQL", "23503"), "deals_client_id_fkey")))
                .when(clientRepository).deleteById(1L);
        mvc.perform(delete("/api/clients/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_IN_USE"))
                .andExpect(content().string(not(containsString("private"))));
    }

    @Test
    void batchSaveIntegrityErrorIsHandledByAdvice() throws Exception {
        when(dealRepository.findByStatus(DealStatus.NEW)).thenReturn(List.of(new Deal()));
        when(dealRepository.saveAll(any())).thenThrow(new DataIntegrityViolationException("private",
                new ConstraintViolationException("private", new SQLException("private SQL", "23503"),
                        "deals_client_id_fkey")));
        mvc.perform(post("/api/deals/process-new"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                .andExpect(content().string(not(containsString("private"))));
    }
}
