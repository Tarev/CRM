package com.example.crm.console;

/**
 * Один раздел главного меню.
 */
public interface MenuSection {

    String getTitle();

    /** Запускает раздел и работает, пока пользователь не вернется назад. */
    void run();
}
