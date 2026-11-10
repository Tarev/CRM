package com.example.crm.console;

import com.example.crm.model.Client;
import com.example.crm.model.Company;
import com.example.crm.model.Contact;
import com.example.crm.model.Deal;
import com.example.crm.model.Note;
import com.example.crm.model.Product;
import com.example.crm.model.Task;
import com.example.crm.model.user.AdminUser;
import com.example.crm.model.user.CustomerUser;
import com.example.crm.model.user.ManagerUser;
import com.example.crm.model.user.User;
import com.example.crm.service.ClientService;
import com.example.crm.service.CompanyService;
import com.example.crm.service.ContactService;
import com.example.crm.service.DealService;
import com.example.crm.service.NoteService;
import com.example.crm.service.ProductService;
import com.example.crm.service.TaskService;
import com.example.crm.service.UserService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Пункт меню, который заполняет систему тестовыми данными.
 */
@Component
@Order(9)
public class DemoDataMenu implements MenuSection {

    private final ClientService clientService;
    private final CompanyService companyService;
    private final ContactService contactService;
    private final ProductService productService;
    private final DealService dealService;
    private final TaskService taskService;
    private final NoteService noteService;
    private final UserService userService;

    public DemoDataMenu(ClientService clientService, CompanyService companyService,
                        ContactService contactService, ProductService productService,
                        DealService dealService, TaskService taskService,
                        NoteService noteService, UserService userService) {
        this.clientService = clientService;
        this.companyService = companyService;
        this.contactService = contactService;
        this.productService = productService;
        this.dealService = dealService;
        this.taskService = taskService;
        this.noteService = noteService;
        this.userService = userService;
    }

    @Override
    public String getTitle() {
        return "Заполнить демо-данными";
    }

    @Override
    public void run() {
        if (!userService.findAll().isEmpty()) {
            System.out.println("Демо-данные уже загружены или в системе есть пользователи");
            return;
        }
        Company romashka = companyService.add(new Company("Ромашка", "Москва"));
        Company vector = companyService.add(new Company("Вектор", "Казань"));

        Client ivan = clientService.add(new Client("Иван Петров", "ivan@mail.ru", "+7-900-111-11-11", romashka.getId()));
        Client anna = clientService.add(new Client("Анна Сидорова", "anna@mail.ru", "+7-900-222-22-22", vector.getId()));
        Client oleg = clientService.add(new Client("Олег Смирнов", "oleg@mail.ru", "+7-900-333-33-33", null));

        contactService.add(new Contact("Мария Кузнецова", "Директор", "+7-900-444-44-44", romashka.getId()));
        contactService.add(new Contact("Павел Соколов", "Бухгалтер", "+7-900-555-55-55", vector.getId()));

        Product crm = productService.add(new Product("Лицензия CRM", new BigDecimal("15000.00")));
        Product support = productService.add(new Product("Поддержка", new BigDecimal("5000.00")));
        Product training = productService.add(new Product("Обучение", new BigDecimal("8000.00")));

        User admin = userService.add(new AdminUser("admin", "Администратор"));
        User manager = userService.add(new ManagerUser("manager", "Менеджер Алексей", "Продажи"));
        userService.add(new CustomerUser("ivan", "Иван Петров", ivan.getId()));

        Deal deal1 = dealService.add(new Deal("Покупка CRM", ivan.getId(), crm.getId(), new BigDecimal("15000.00")));
        dealService.add(new Deal("Поддержка на год", anna.getId(), support.getId(), new BigDecimal("60000.00")));
        dealService.add(new Deal("Обучение сотрудников", oleg.getId(), training.getId(), new BigDecimal("8000.00")));
        dealService.add(new Deal("Доп. лицензии", ivan.getId(), crm.getId(), new BigDecimal("30000.00")));

        taskService.add(new Task("Позвонить клиенту", deal1.getId(), manager.getId(), LocalDate.now().minusDays(1)));
        taskService.add(new Task("Отправить счет", deal1.getId(), manager.getId(), LocalDate.now().plusDays(3)));

        noteService.add(new Note(deal1.getId(), "Клиент просит скидку"));

        System.out.println("Демо-данные загружены (администратор: " + admin.getLogin() + ")");
    }
}
