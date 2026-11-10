package com.example.crm.console;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Главное меню. Запускается после старта Spring, если crm.console.enabled=true.
 */
@Component
@ConditionalOnProperty(name = "crm.console.enabled", havingValue = "true", matchIfMissing = true)
public class ConsoleMenu implements CommandLineRunner {

    private final List<MenuSection> sections;
    private final ConsoleInput input;

    public ConsoleMenu(List<MenuSection> sections, ConsoleInput input) {
        this.sections = sections;
        this.input = input;
    }

    @Override
    public void run(String... args) {
        boolean exit = false;
        while (!exit) {
            System.out.println();
            System.out.println("===== CRM =====");
            for (int i = 0; i < sections.size(); i++) {
                System.out.println((i + 1) + ". " + sections.get(i).getTitle());
            }
            System.out.println("0. Выход");
            int choice = input.readInt("Выбор: ");
            if (choice == 0) {
                exit = true;
            } else if (choice > 0 && choice <= sections.size()) {
                sections.get(choice - 1).run();
            } else {
                System.out.println("Нет такого пункта");
            }
        }
        System.out.println("До свидания!");
    }
}
