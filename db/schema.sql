-- Схема базы данных CRM (PostgreSQL 16)
-- Запуск в pgAdmin: Query Tool -> открыть файл -> F5 (или psql -f schema.sql).
-- Скрипт можно запускать повторно: старые таблицы удаляются.

DROP TABLE IF EXISTS notes, tasks, deals, users, contacts, clients, products, companies CASCADE;

-- Компания (организация, объединяющая клиентов и контакты)
CREATE TABLE companies (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    city VARCHAR(100) NOT NULL
);

-- Клиент (юридическое или физическое лицо)
CREATE TABLE clients (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE CHECK (email LIKE '%@%'),
    name        VARCHAR(255) NOT NULL,
    phone       VARCHAR(20) CHECK (phone ~ '^\+7\(\d{3}\)\d{3}-\d{2}-\d{2}$'),
    client_type VARCHAR(10) NOT NULL DEFAULT 'INDIVIDUAL' CHECK (client_type IN ('INDIVIDUAL', 'COMPANY')),
    company_id  BIGINT REFERENCES companies (id) ON DELETE SET NULL
);

-- Контактное лицо компании
CREATE TABLE contacts (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE CHECK (email LIKE '%@%'),
    full_name  VARCHAR(255) NOT NULL,
    position   VARCHAR(100),
    company_id BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE
);

-- Продукт (товар или услуга)
CREATE TABLE products (
    id      BIGSERIAL PRIMARY KEY,
    article VARCHAR(50) NOT NULL UNIQUE,
    name    VARCHAR(255) NOT NULL,
    price   DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (price >= 0)
);

-- Сделка
CREATE TABLE deals (
    id          BIGSERIAL PRIMARY KEY,
    deal_number VARCHAR(50) NOT NULL UNIQUE,
    title       VARCHAR(255) NOT NULL,
    amount      DECIMAL(12, 2) NOT NULL DEFAULT 0.00 CHECK (amount >= 0),
    status      VARCHAR(20) NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW', 'IN_PROGRESS', 'WON', 'LOST')),
    client_id   BIGINT NOT NULL REFERENCES clients (id),
    product_id  BIGINT NOT NULL REFERENCES products (id)
);

-- Пользователь системы. Колонка role определяет класс: AdminUser, ManagerUser, CustomerUser
CREATE TABLE users (
    id        BIGSERIAL PRIMARY KEY,
    login     VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role      VARCHAR(20) NOT NULL DEFAULT 'MANAGER' CHECK (role IN ('ADMIN', 'MANAGER', 'CUSTOMER')),
    client_id BIGINT UNIQUE REFERENCES clients (id) ON DELETE SET NULL,
    -- клиент может быть привязан только к пользователю с ролью CUSTOMER
    CHECK (client_id IS NULL OR role = 'CUSTOMER')
);

-- Задача менеджера (сделка необязательна)
CREATE TABLE tasks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    deadline    DATE NOT NULL,
    done        BOOLEAN NOT NULL DEFAULT FALSE,
    deal_id     BIGINT REFERENCES deals (id) ON DELETE SET NULL,
    assignee_id BIGINT NOT NULL REFERENCES users (id)
);

-- Заметка к сделке
CREATE TABLE notes (
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    text       TEXT NOT NULL,
    deal_id    BIGINT NOT NULL REFERENCES deals (id) ON DELETE CASCADE
);

-- Индексы по внешним ключам
CREATE INDEX idx_clients_company ON clients (company_id);
CREATE INDEX idx_contacts_company ON contacts (company_id);
CREATE INDEX idx_deals_client ON deals (client_id);
CREATE INDEX idx_deals_product ON deals (product_id);
CREATE INDEX idx_tasks_deal ON tasks (deal_id);
CREATE INDEX idx_tasks_assignee ON tasks (assignee_id);
CREATE INDEX idx_notes_deal ON notes (deal_id);
