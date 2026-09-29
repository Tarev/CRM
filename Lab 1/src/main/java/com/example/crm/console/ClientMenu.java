package com.example.crm.console;

import com.example.crm.model.Client;
import com.example.crm.service.ClientService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ClientMenu implements MenuSection {

    private final ClientService clientService;
    private final ConsoleInput input;

    public ClientMenu(ClientService clientService, ConsoleInput input) {
        this.clientService = clientService;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Клиенты";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Клиенты ---");
            System.out.println("1. Показать всех");
            System.out.println("2. Добавить");
            System.out.println("3. Найти по имени");
            System.out.println("4. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(clientService.describeAll());
                    break;
                case 2:
                    String name = input.readString("Имя: ");
                    String email = input.readString("Email: ");
                    String phone = input.readString("Телефон: ");
                    long companyId = input.readLong("id компании (0 - без компании): ");
                    Long company = companyId == 0 ? null : companyId;
                    try {
                        System.out.println("Добавлено: " + clientService.add(new Client(name, email, phone, company)));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println(clientService.describe(clientService.findByName(input.readString("Часть имени: "))));
                    break;
                case 4:
                    if (clientService.deleteById(input.readLong("id для удаления: "))) {
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
}
