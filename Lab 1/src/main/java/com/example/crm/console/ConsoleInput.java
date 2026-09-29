package com.example.crm.console;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Чтение данных из консоли с повторным запросом при неверном вводе.
 */
@Component
public class ConsoleInput {

    private final Scanner scanner = new Scanner(System.in);

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            String text = readString(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число");
            }
        }
    }

    public long readLong(String prompt) {
        while (true) {
            String text = readString(prompt);
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число");
            }
        }
    }

    public BigDecimal readMoney(String prompt) {
        while (true) {
            String text = readString(prompt).replace(',', '.');
            try {
                BigDecimal value = new BigDecimal(text);
                if (value.signum() >= 0) {
                    return value;
                }
                System.out.println("Число не должно быть отрицательным");
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести число, например 1500.50");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String text = readString(prompt);
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Формат даты: ГГГГ-ММ-ДД, например 2026-10-15");
            }
        }
    }
}
