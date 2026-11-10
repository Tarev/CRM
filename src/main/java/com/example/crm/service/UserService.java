package com.example.crm.service;

import com.example.crm.dto.UserRequest;
import com.example.crm.model.Client;
import com.example.crm.model.user.AdminUser;
import com.example.crm.model.user.CustomerUser;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.User;
import com.example.crm.model.user.UserRole;
import com.example.crm.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractService<User> {

    private final UserRepository userRepository;
    private final ClientService clientService;

    public UserService(UserRepository userRepository, ClientService clientService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.clientService = clientService;
    }

    /** Если роль не указана, возвращает всех пользователей. */
    public List<User> findByRole(UserRole role) {
        if (role == null) {
            return findAll();
        }
        return userRepository.findByRole(role.name());
    }

    /** Создает пользователя нужного класса по роли. Возвращает empty, если данные не подходят (см. fillAndSave). */
    public Optional<User> create(UserRequest request) {
        User user;
        if (request.getRole() == UserRole.ADMIN) {
            user = new AdminUser();
        } else if (request.getRole() == UserRole.MANAGER) {
            user = new ManagerUser();
        } else {
            user = new CustomerUser();
        }
        return fillAndSave(user, request);
    }

    /** Обновляет пользователя. Роль менять нельзя: тогда вернется empty. */
    public Optional<User> update(Long id, UserRequest request) {
        Optional<User> user = findById(id);
        if (user.isEmpty() || user.get().getRole() != request.getRole()) {
            return Optional.empty();
        }
        return fillAndSave(user.get(), request);
    }

    /**
     * Заполняет поля и сохраняет. Возвращает empty, если clientId указан у не-CUSTOMER
     * или клиент с таким id не найден.
     */
    private Optional<User> fillAndSave(User user, UserRequest request) {
        user.setLogin(request.getLogin());
        user.setFullName(request.getFullName());
        if (user instanceof CustomerUser) {
            CustomerUser customer = (CustomerUser) user;
            customer.setClient(null);
            if (request.getClientId() != null) {
                Optional<Client> client = clientService.findById(request.getClientId());
                if (client.isEmpty()) {
                    return Optional.empty();
                }
                customer.setClient(client.get());
            }
        } else if (request.getClientId() != null) {
            return Optional.empty();
        }
        return Optional.of(save(user));
    }
}
