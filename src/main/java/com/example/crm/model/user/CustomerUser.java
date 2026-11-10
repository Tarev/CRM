package com.example.crm.model.user;

import com.example.crm.model.Client;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
public class CustomerUser extends User {

    /** Клиент, к которому привязан аккаунт (связь 1:1, может быть null). */
    @OneToOne
    @JoinColumn(name = "client_id", unique = true)
    private Client client;

    public CustomerUser(String login, String fullName, Client client) {
        super(login, fullName);
        this.client = client;
    }

    @Override
    public UserRole getRole() {
        return UserRole.CUSTOMER;
    }

    @Override
    public String getAccessDescription() {
        return "Только просмотр своих данных";
    }
}
