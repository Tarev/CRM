# CRM system

Учебный проект: клиент-серверная CRM-система на Spring Boot + Maven.

## Что делает система

CRM помогает компании работать с клиентами. Система хранит и обрабатывает:

- клиентов и компании, контактных лиц компаний;
- продукты (товары и услуги);
- сделки (статусы NEW, IN_PROGRESS, WON, LOST);
- задачи менеджеров и заметки к сделкам;
- пользователей с ролями ADMIN, MANAGER, CUSTOMER.

### Лабораторная работа №1

- данные хранятся в памяти (`Map`, `List`);
- добавление, поиск, удаление и вывод объектов;
- многопоточная обработка сделок (потоки `deal-worker-1`, `deal-worker-2`, ...), имена потоков видны в логе;
- консольное меню;
- unit-тесты (JUnit 5).

## Структура проекта (пакеты `com.example.crm`)

| Пакет | Для чего |
|---|---|
| `model` | Сущности предметной области. Базовый класс `BaseEntity` (id), от него `Client`, `Company`, `Contact`, `Product`, `Deal`, `Task`, `Note`; `DealStatus` - enum статусов |
| `model.user` | Пользователи: enum `UserRole`, абстрактный `User` и наследники `AdminUser`, `ManagerUser`, `CustomerUser` |
| `repository` | Хранилища: интерфейс `EntityRepository` и реализация `InMemoryRepository` (хранит данные в `Map`) |
| `service` | Логика: `AbstractService` (общие операции) и по одному сервису на сущность с поиском по полям |
| `processing` | Многопоточная обработка сделок: `DealProcessor`, `ParallelDealProcessor` (ExecutorService), `NamedThreadFactory` (имена потоков) |
| `console` | Консольное меню: `ConsoleMenu` (главное), `MenuSection` и по одному разделу на сущность, `ConsoleInput` (ввод с проверкой), `DemoDataMenu` (тестовые данные) |
| `config` | `RepositoryConfig` - создает хранилища для Spring |
| `util` | `Validation` - проверки полей |

Классы разделены по одной задаче: сущности только хранят данные, репозитории - хранят объекты, сервисы - ищут и обрабатывают, меню - общается с пользователем.

## Запуск

Требуется Java 17 и Maven.

```
mvn spring-boot:run   # запуск с консольным меню
mvn verify            # сборка и unit-тесты
```

В IntelliJ IDEA можно просто запустить класс `CrmApplication`. В меню удобно начать с пункта «Заполнить демо-данными».
Если русские буквы в консоли не отображаются, добавьте VM options: `-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8`.

Количество потоков обработки настраивается в `application.properties` (`crm.worker-threads`).

## CI

`.gitlab-ci.yml` запускает `mvn verify` (сборка + unit-тесты) при каждом push.
