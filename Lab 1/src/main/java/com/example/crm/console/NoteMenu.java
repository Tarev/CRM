package com.example.crm.console;

import com.example.crm.model.Note;
import com.example.crm.service.NoteService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(7)
public class NoteMenu implements MenuSection {

    private final NoteService noteService;
    private final ConsoleInput input;

    public NoteMenu(NoteService noteService, ConsoleInput input) {
        this.noteService = noteService;
        this.input = input;
    }

    @Override
    public String getTitle() {
        return "Заметки";
    }

    @Override
    public void run() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Заметки ---");
            System.out.println("1. Показать все");
            System.out.println("2. Добавить");
            System.out.println("3. Заметки по id сделки");
            System.out.println("4. Удалить по id");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ");
            switch (choice) {
                case 1:
                    System.out.println(noteService.describeAll());
                    break;
                case 2:
                    long dealId = input.readLong("id сделки: ");
                    String text = input.readString("Текст: ");
                    try {
                        System.out.println("Добавлено: " + noteService.add(new Note(dealId, text)));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println(noteService.describe(noteService.findByDealId(input.readLong("id сделки: "))));
                    break;
                case 4:
                    if (noteService.deleteById(input.readLong("id для удаления: "))) {
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
