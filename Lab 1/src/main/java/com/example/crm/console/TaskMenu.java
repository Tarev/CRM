package com.example.crm.console;

import com.example.crm.model.Task;
import com.example.crm.service.TaskService;

import java.time.LocalDate;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class TaskMenu implements MenuSection {

    private final TaskService taskService;
    private final ConsoleInput input;

    public TaskMenu(TaskService taskService, ConsoleInput input) {
        this.taskService = taskService;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Задачи";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Задачи ---");
            System.out.println("1. Показать все");
            System.out.println("2. Добавить");
            System.out.println("3. Задачи менеджера");
            System.out.println("4. Просроченные задачи");
            System.out.println("5. Отметить выполненной");
            System.out.println("6. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(taskService.describeAll());
                    break;
                case 2:
                    String title = input.readString("Название: ");
                    long dealId = input.readLong("id сделки: ");
                    long assigneeId = input.readLong("id менеджера: ");
                    LocalDate deadline = input.readDate("Срок (ГГГГ-ММ-ДД): ");
                    try {
                        System.out.println("Добавлено: " + taskService.add(new Task(title, dealId, assigneeId, deadline)));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println(taskService.describe(taskService.findByAssigneeId(input.readLong("id менеджера: "))));
                    break;
                case 4:
                    System.out.println(taskService.describe(taskService.findOverdue(LocalDate.now())));
                    break;
                case 5:
                    if (taskService.markDone(input.readLong("id задачи: "))) {
                        System.out.println("Задача выполнена");
                    } else {
                        System.out.println("Задача не найдена");
                    }
                    break;
                case 6:
                    if (taskService.deleteById(input.readLong("id для удаления: "))) {
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
