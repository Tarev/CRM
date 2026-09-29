package com.example.crm.model.user;

import com.example.crm.util.Validation;

public class ManagerUser extends User {

    private final String department;

    public ManagerUser(String login, String fullName, String department) {
        super(login, fullName);
        this.department = Validation.requireNotBlank(department, "department");
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public UserRole getRole() {
        return UserRole.MANAGER;
    }

    @Override
    public String getAccessDescription() {
        return "Работа с клиентами, сделками, задачами и заметками (без управления пользователями), отдел: " + department;
    }
}
