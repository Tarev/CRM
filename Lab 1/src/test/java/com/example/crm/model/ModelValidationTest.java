package com.example.crm.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelValidationTest {

    @Test
    void clientWithBlankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Client("  ", "a@mail.ru", "123", null));
    }

    @Test
    void clientNameIsTrimmed() {
        Client client = new Client("  Ivan  ", "a@mail.ru", "123", null);

        assertEquals("Ivan", client.getName());
    }

    @Test
    void productWithNegativePriceIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Product("CRM", new BigDecimal("-1")));
    }

    @Test
    void newDealHasStatusNew() {
        Deal deal = new Deal("Deal", 1L, 1L, BigDecimal.TEN);

        assertEquals(DealStatus.NEW, deal.getStatus());
    }

    @Test
    void dealWithoutClientIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Deal("Deal", null, 1L, BigDecimal.TEN));
    }

    @Test
    void newTaskIsNotDone() {
        Task task = new Task("Call", 1L, 1L, LocalDate.now());

        assertFalse(task.isDone());
    }

    @Test
    void contactWithoutCompanyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Contact("Ivan", "Boss", "123", null));
    }

    @Test
    void noteWithEmptyTextIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Note(1L, ""));
    }
}
