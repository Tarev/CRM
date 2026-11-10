package com.example.crm.model.user;

import com.example.crm.model.BaseEntity;
import com.example.crm.util.Validation;

/**
 * Абстрактный пользователь системы. Роль определяется классом-наследником.
 */
public abstract class User extends BaseEntity {

    private final String login;
    private final String fullName;

    protected User(String login, String fullName) {
        this.login = Validation.requireNotBlank(login, "login");
        this.fullName = Validation.requireNotBlank(fullName, "fullName");
    }

    public String getLogin() {
        return login;
    }

    public String getFullName() {
        return fullName;
    }

    public abstract UserRole getRole();

    /** Краткое описание прав роли (позже будет основой для Spring Security). */
    public abstract String getAccessDescription();

    @Override
    public String toString() {
        return "Пользователь #" + getId() + ": " + login + " (" + fullName + "), роль " + getRole();
    }
}
