package com.example.crm.service;

import com.example.crm.model.user.User;
import com.example.crm.model.user.UserRole;
import com.example.crm.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractService<User> {

    public UserService(EntityRepository<User> repository) {
        super(repository);
    }

    /** Добавляет пользователя; логин должен быть уникальным. */
    @Override
    public User add(User user) {
        if (findByLogin(user.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с логином " + user.getLogin() + " уже существует");
        }
        return super.add(user);
    }

    public Optional<User> findByLogin(String login) {
        for (User user : findAll()) {
            if (user.getLogin().equalsIgnoreCase(login)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public List<User> findByRole(UserRole role) {
        List<User> result = new ArrayList<>();
        for (User user : findAll()) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }
}
