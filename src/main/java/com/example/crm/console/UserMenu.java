package com.example.crm.console;

import com.example.crm.model.user.AdminUser;
import com.example.crm.model.user.CustomerUser;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.User;
import com.example.crm.model.user.UserRole;
import com.example.crm.service.UserService;

import java.util.Optional;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(8)
public class UserMenu implements MenuSection {

    private final UserService userService;
    private final ConsoleInput input;

    public UserMenu(UserService userService, ConsoleInput input) {
        this.userService = userService;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Пользователи";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Пользователи ---");
            System.out.println("1. Показать всех");
            System.out.println("2. Добавить");
            System.out.println("3. Найти по роли");
            System.out.println("4. Найти по логину");
            System.out.println("5. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(userService.describeAll());
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    System.out.println(userService.describe(userService.findByRole(readRole())));
                    break;
                case 4:
                    Optional<User> found = userService.findByLogin(input.readString("Логин: "));
                    if (found.isPresent()) {
                        System.out.println(found.get() + ". Права: " + found.get().getAccessDescription());
                    } else {
                        System.out.println("Пользователь не найден");
                    }
                    break;
                case 5:
                    if (userService.deleteById(input.readLong("id для удаления: "))) {
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

    private UserRole readRole() {
        while (true) {
            String text = input.readString("Роль (ADMIN, MANAGER, CUSTOMER): ").toUpperCase();
            try {
                return UserRole.valueOf(text);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверная роль");
            }
        }
    }

    private void addUser() {
        UserRole role = readRole();
        String login = input.readString("Логин: ");
        String fullName = input.readString("Имя: ");
        try {
            User user;
            if (role == UserRole.ADMIN) {
                user = new AdminUser(login, fullName);
            } else if (role == UserRole.MANAGER) {
                user = new ManagerUser(login, fullName, input.readString("Отдел: "));
            } else {
                long clientId = input.readLong("id клиента (0 - нет): ");
                user = new CustomerUser(login, fullName, clientId == 0 ? null : clientId);
            }
            System.out.println("Добавлено: " + userService.add(user));
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
