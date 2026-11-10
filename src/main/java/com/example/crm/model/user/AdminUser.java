package com.example.crm.model.user;

public class AdminUser extends User {

    public AdminUser(String login, String fullName) {
        super(login, fullName);
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }

    @Override
    public String getAccessDescription() {
        return "Полный доступ: просмотр, создание, изменение, удаление, управление пользователями";
    }
}
