-- Тестовые данные CRM. Запускать после schema.sql.

INSERT INTO companies (name, city) VALUES
    ('Ромашка', 'Москва'),
    ('Вектор', 'Казань'),
    ('Альфа', 'Нижний Новгород');

INSERT INTO clients (email, name, phone, client_type, company_id) VALUES
    ('ivan@romashka.ru', 'Иван Петров', '+7(900)111-11-11', 'COMPANY',
        (SELECT id FROM companies WHERE name = 'Ромашка')),
    ('anna@vector.ru', 'Анна Сидорова', '+7(900)222-22-22', 'COMPANY',
        (SELECT id FROM companies WHERE name = 'Вектор')),
    ('oleg@mail.ru', 'Олег Смирнов', '+7(900)333-33-33', 'INDIVIDUAL', NULL),
    ('maria@mail.ru', 'Мария Иванова', NULL, 'INDIVIDUAL', NULL);

INSERT INTO contacts (email, full_name, position, company_id) VALUES
    ('director@romashka.ru', 'Мария Кузнецова', 'Директор', (SELECT id FROM companies WHERE name = 'Ромашка')),
    ('buh@vector.ru', 'Павел Соколов', 'Бухгалтер', (SELECT id FROM companies WHERE name = 'Вектор')),
    ('sales@alfa.ru', 'Елена Орлова', NULL, (SELECT id FROM companies WHERE name = 'Альфа'));

INSERT INTO products (article, name, price) VALUES
    ('CRM-001', 'Лицензия CRM', 15000.00),
    ('SUP-001', 'Техподдержка', 5000.00),
    ('EDU-001', 'Обучение сотрудников', 8000.00);

INSERT INTO users (login, full_name, role, client_id) VALUES
    ('admin', 'Администратор', 'ADMIN', NULL),
    ('manager1', 'Менеджер Алексей', 'MANAGER', NULL),
    ('manager2', 'Менеджер Ольга', 'MANAGER', NULL),
    ('ivan', 'Иван Петров', 'CUSTOMER', (SELECT id FROM clients WHERE email = 'ivan@romashka.ru'));

INSERT INTO deals (deal_number, title, amount, status, client_id, product_id) VALUES
    ('D-2026-001', 'Покупка CRM', 15000.00, 'WON',
        (SELECT id FROM clients WHERE email = 'ivan@romashka.ru'), (SELECT id FROM products WHERE article = 'CRM-001')),
    ('D-2026-002', 'Поддержка на год', 60000.00, 'IN_PROGRESS',
        (SELECT id FROM clients WHERE email = 'anna@vector.ru'), (SELECT id FROM products WHERE article = 'SUP-001')),
    ('D-2026-003', 'Обучение сотрудников', 8000.00, 'NEW',
        (SELECT id FROM clients WHERE email = 'oleg@mail.ru'), (SELECT id FROM products WHERE article = 'EDU-001')),
    ('D-2026-004', 'Дополнительные лицензии', 30000.00, 'NEW',
        (SELECT id FROM clients WHERE email = 'ivan@romashka.ru'), (SELECT id FROM products WHERE article = 'CRM-001')),
    ('D-2026-005', 'Консультация', 5000.00, 'LOST',
        (SELECT id FROM clients WHERE email = 'maria@mail.ru'), (SELECT id FROM products WHERE article = 'SUP-001'));

INSERT INTO tasks (title, deadline, done, deal_id, assignee_id) VALUES
    ('Позвонить клиенту', CURRENT_DATE - 2, FALSE,
        (SELECT id FROM deals WHERE deal_number = 'D-2026-003'), (SELECT id FROM users WHERE login = 'manager1')),
    ('Отправить счет', CURRENT_DATE + 3, FALSE,
        (SELECT id FROM deals WHERE deal_number = 'D-2026-004'), (SELECT id FROM users WHERE login = 'manager1')),
    ('Подписать договор', CURRENT_DATE + 7, FALSE,
        (SELECT id FROM deals WHERE deal_number = 'D-2026-002'), (SELECT id FROM users WHERE login = 'manager2')),
    ('Подготовить отчет за месяц', CURRENT_DATE + 14, FALSE, NULL, (SELECT id FROM users WHERE login = 'manager2')),
    ('Провести встречу', CURRENT_DATE - 10, TRUE,
        (SELECT id FROM deals WHERE deal_number = 'D-2026-001'), (SELECT id FROM users WHERE login = 'manager1'));

INSERT INTO notes (created_at, text, deal_id) VALUES
    (NOW() - INTERVAL '3 days', 'Клиент просит скидку', (SELECT id FROM deals WHERE deal_number = 'D-2026-001')),
    (NOW() - INTERVAL '2 days', 'Договорились о цене', (SELECT id FROM deals WHERE deal_number = 'D-2026-001')),
    (NOW() - INTERVAL '1 day', 'Ожидаем оплату', (SELECT id FROM deals WHERE deal_number = 'D-2026-002')),
    (NOW(), 'Клиент не берет трубку', (SELECT id FROM deals WHERE deal_number = 'D-2026-003'));
