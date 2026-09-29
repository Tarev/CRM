package com.example.crm.console;

import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import com.example.crm.processing.DealProcessor;
import com.example.crm.service.DealService;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class DealMenu implements MenuSection {

    private final DealService dealService;
    private final DealProcessor dealProcessor;
    private final ConsoleInput input;

    public DealMenu(DealService dealService, DealProcessor dealProcessor, ConsoleInput input) {
        this.dealService = dealService;
        this.dealProcessor = dealProcessor;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Сделки";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Сделки ---");
            System.out.println("1. Показать все");
            System.out.println("2. Добавить");
            System.out.println("3. Найти по статусу");
            System.out.println("4. Сделки клиента");
            System.out.println("5. Изменить статус");
            System.out.println("6. Сумма по статусу");
            System.out.println("7. Обработать новые сделки в нескольких потоках");
            System.out.println("8. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(dealService.describeAll());
                    break;
                case 2:
                    String title = input.readString("Название: ");
                    long clientId = input.readLong("id клиента: ");
                    long productId = input.readLong("id продукта: ");
                    BigDecimal amount = input.readMoney("Сумма: ");
                    try {
                        System.out.println("Добавлено: " + dealService.add(new Deal(title, clientId, productId, amount)));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    DealStatus status = readStatus();
                    System.out.println(dealService.describe(dealService.findByStatus(status)));
                    break;
                case 4:
                    System.out.println(dealService.describe(dealService.findByClientId(input.readLong("id клиента: "))));
                    break;
                case 5:
                    long id = input.readLong("id сделки: ");
                    DealStatus newStatus = readStatus();
                    if (dealService.changeStatus(id, newStatus)) {
                        System.out.println("Статус изменен");
                    } else {
                        System.out.println("Сделка не найдена");
                    }
                    break;
                case 6:
                    DealStatus totalStatus = readStatus();
                    System.out.println("Сумма: " + dealService.getTotalAmount(totalStatus));
                    break;
                case 7:
                    Set<String> names = dealProcessor.process(dealService.findByStatus(DealStatus.NEW));
                    System.out.println("Обработка завершена, работали потоки: " + names);
                    break;
                case 8:
                    if (dealService.deleteById(input.readLong("id для удаления: "))) {
                        System.out.println("Удалено");
                    } else {
                        System.out.println("Объект с таким id не найден");
                    }
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Нет такого пункта");
            }
        }
    }

    private DealStatus readStatus() {
        while (true) {
            String text = input.readString("Статус (NEW, IN_PROGRESS, WON, LOST): ").toUpperCase();
            try {
                return DealStatus.valueOf(text);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный статус");
            }
        }
    }
}
