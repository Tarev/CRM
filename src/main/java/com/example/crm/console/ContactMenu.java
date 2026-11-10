package com.example.crm.console;

import com.example.crm.model.Contact;
import com.example.crm.service.ContactService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class ContactMenu implements MenuSection {

    private final ContactService contactService;
    private final ConsoleInput input;

    public ContactMenu(ContactService contactService, ConsoleInput input) {
        this.contactService = contactService;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Контакты";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Контакты ---");
            System.out.println("1. Показать все");
            System.out.println("2. Добавить");
            System.out.println("3. Найти по id компании");
            System.out.println("4. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(contactService.describeAll());
                    break;
                case 2:
                    String fullName = input.readString("ФИО: ");
                    String position = input.readString("Должность: ");
                    String phone = input.readString("Телефон: ");
                    long companyId = input.readLong("id компании: ");
                    try {
                        System.out.println("Добавлено: " + contactService.add(new Contact(fullName, position, phone, companyId)));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println(contactService.describe(contactService.findByCompanyId(input.readLong("id компании: "))));
                    break;
                case 4:
                    if (contactService.deleteById(input.readLong("id для удаления: "))) {
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
