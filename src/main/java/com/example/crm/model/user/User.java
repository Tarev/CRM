package com.example.crm.model.user;

import com.example.crm.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Абстрактный пользователь системы. Все пользователи лежат в одной таблице users,
 * колонка role показывает, какой это класс-наследник (ADMIN, MANAGER, CUSTOMER).
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING, length = 20)
@Getter
@Setter
@NoArgsConstructor
public abstract class User extends BaseEntity {

    @Column(name = "login", nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    protected User(String login, String fullName) {
        this.login = login;
        this.fullName = fullName;
    }

    public abstract UserRole getRole();

    /** Краткое описание прав роли (позже будет основой для Spring Security). */
    public abstract String getAccessDescription();
}
