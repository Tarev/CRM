package com.example.crm.model.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
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
