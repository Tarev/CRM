package com.example.crm.model;

import com.example.crm.dto.ClientRequest;
import com.example.crm.dto.DealRequest;
import com.example.crm.dto.TaskRequest;
import com.example.crm.dto.UserRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void correctCompanyHasNoErrors() {
        assertTrue(validator.validate(new Company("Romashka", "Moscow")).isEmpty());
    }

    @Test
    void companyWithBlankNameIsRejected() {
        assertEquals(1, validator.validate(new Company(" ", "Moscow")).size());
    }

    @Test
    void productWithNegativePriceIsRejected() {
        assertEquals(1, validator.validate(new Product("A-1", "CRM", new BigDecimal("-1"))).size());
    }

    @Test
    void productWithThreeDigitsAfterPointIsRejected() {
        assertEquals(1, validator.validate(new Product("A-1", "CRM", new BigDecimal("1.234"))).size());
    }

    @Test
    void clientRequestWithWrongEmailAndPhoneHasTwoErrors() {
        ClientRequest request = new ClientRequest();
        request.setEmail("not-an-email");
        request.setName("Ivan");
        request.setPhone("89001234567");

        assertEquals(2, validator.validate(request).size());
    }

    @Test
    void clientRequestWithCorrectPhoneIsValid() {
        ClientRequest request = new ClientRequest();
        request.setEmail("ivan@mail.ru");
        request.setName("Ivan");
        request.setPhone("+7(900)123-45-67");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void dealRequestRequiresClientAndProduct() {
        DealRequest request = new DealRequest();
        request.setDealNumber("D-1");
        request.setTitle("Deal");
        request.setAmount(BigDecimal.TEN);

        assertEquals(2, validator.validate(request).size());
    }

    @Test
    void taskRequestWithPastDeadlineIsRejected() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Call");
        request.setDeadline(LocalDate.now().minusDays(1));
        request.setAssigneeId(1L);

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void taskRequestWithTodayDeadlineIsValid() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Call");
        request.setDeadline(LocalDate.now());
        request.setAssigneeId(1L);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void userRequestWithoutRoleIsRejected() {
        UserRequest request = new UserRequest();
        request.setLogin("admin");
        request.setFullName("Admin");

        assertEquals(1, validator.validate(request).size());
    }
}
