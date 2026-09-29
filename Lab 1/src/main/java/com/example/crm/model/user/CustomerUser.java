package com.example.crm.model.user;

public class CustomerUser extends User {

    /** id клиента, к которому привязан аккаунт (может быть null). */
    private final Long clientId;

    public CustomerUser(String login, String fullName, Long clientId) {
        super(login, fullName);
        this.clientId = clientId;
    }

    public Long getClientId() {
        return clientId;
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
