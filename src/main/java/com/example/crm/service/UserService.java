package com.example.crm.service;

import com.example.crm.dto.UserRequest;
import com.example.crm.exception.BusinessRuleViolationException;
import com.example.crm.exception.ResourceInUseException;
import com.example.crm.model.Client;
import com.example.crm.model.user.AdminUser;
import com.example.crm.model.user.CustomerUser;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.User;
import com.example.crm.model.user.UserRole;
import com.example.crm.repository.TaskRepository;
import com.example.crm.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends AbstractService<User> {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ClientService clientService;

    public UserService(UserRepository userRepository, ClientService clientService, TaskRepository taskRepository) {
        super(userRepository, "Пользователь");
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.clientService = clientService;
    }

    /** Если роль не указана, возвращает всех пользователей. */
    public List<User> findByRole(UserRole role) {
        if (role == null) {
            return findAll();
        }
        return userRepository.findByRole(role.name());
    }

    /** Создает пользователя нужного класса по роли. */
    public User create(UserRequest request) {
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

    /** Обновляет пользователя. Роль менять нельзя. */
    public User update(Long id, UserRequest request) {
        User user = getById(id);
        if (user.getRole() != request.getRole()) {
            throw new BusinessRuleViolationException("Изменять роль существующего пользователя нельзя.",
                    "Сохраните текущую роль или создайте другого пользователя.");
        }
        return fillAndSave(user, request);
    }

    /** Сначала проверяет все данные, затем изменяет поля пользователя. */
    private User fillAndSave(User user, UserRequest request) {
        if (request.getRole() == null) {
            throw new BusinessRuleViolationException("Роль пользователя обязательна.",
                    "Укажите ADMIN, MANAGER или CUSTOMER.");
        }
        if (!(user instanceof CustomerUser) && request.getClientId() != null) {
            throw new BusinessRuleViolationException("Привязка к клиенту доступна только для CUSTOMER.",
                    "Уберите clientId или создайте пользователя с ролью CUSTOMER.");
        }
        Client client = null;
        if (request.getClientId() != null) {
            client = clientService.requireReference(request.getClientId());
            requireUnique(userRepository.findCustomerByClientId(request.getClientId()), user.getId(), "clientId");
        }
        requireUnique(userRepository.findByLogin(request.getLogin()), user.getId(), "login");
        user.setLogin(request.getLogin());
        user.setFullName(request.getFullName());
        if (user instanceof CustomerUser customer) {
            customer.setClient(client);
        }
        return save(user);
    }

    @Override
    protected void checkCanDelete(Long id) {
        if (taskRepository.existsByAssigneeId(id)) {
            throw new ResourceInUseException("пользователя", "задачи");
        }
    }
}
