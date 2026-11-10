package com.example.crm.model.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MANAGER")
@NoArgsConstructor
public class ManagerUser extends User {

    public ManagerUser(String login, String fullName) {
        super(login, fullName);
    }

    @Override
    public UserRole getRole() {
        return UserRole.MANAGER;
    }

    @Override
    public String getAccessDescription() {
        return "Работа с клиентами, сделками, задачами и заметками (без управления пользователями)";
    }
}
