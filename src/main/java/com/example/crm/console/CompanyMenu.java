package com.example.crm.console;

import com.example.crm.model.Company;
import com.example.crm.service.CompanyService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class CompanyMenu implements MenuSection {

    private final CompanyService companyService;
    private final ConsoleInput input;

    public CompanyMenu(CompanyService companyService, ConsoleInput input) {
        this.companyService = companyService;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Компании";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Компании ---");
            System.out.println("1. Показать все");
            System.out.println("2. Добавить");
            System.out.println("3. Найти по городу");
            System.out.println("4. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(companyService.describeAll());
                    break;
                case 2:
                    String name = input.readString("Название: ");
                    String city = input.readString("Город: ");
                    try {
                        System.out.println("Добавлено: " + companyService.add(new Company(name, city)));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println(companyService.describe(companyService.findByCity(input.readString("Город: "))));
                    break;
                case 4:
                    if (companyService.deleteById(input.readLong("id для удаления: "))) {
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
