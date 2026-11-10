# CRM system

Учебный проект (курсовая работа по Java): клиент-серверная CRM-система на Spring Boot + Maven.
Проект развивается по лабораторным работам, каждая лабораторная - отдельная ветка (`lab2` - текущая).

## Что делает система

CRM помогает компании работать с клиентами. Система хранит в PostgreSQL и обрабатывает через REST API:

- компании, клиентов (физические и юридические лица), контактные лица компаний;
- продукты (товары и услуги);
- сделки (статусы NEW, IN_PROGRESS, WON, LOST);
- задачи менеджеров и заметки к сделкам;
- пользователей с ролями ADMIN, MANAGER, CUSTOMER.

### Лабораторная работа №2

- база данных PostgreSQL (схема в `db/schema.sql`, тестовые данные в `db/data.sql`);
- доступ к данным через Spring Data JPA, паттерн MVC, Lombok;
- полный CRUD (создание, чтение, обновление, удаление) для всех сущностей через REST API;
- фильтры в запросах `GET` (см. таблицу ниже);
- многопоточная обработка новых сделок из ЛР1 (`POST /api/deals/process-new`);
- unit-тесты на `@SpringBootTest` + Mockito: к базе данных не обращаются.

## Структура проекта (пакеты `com.example.crm`)

| Пакет | Для чего |
|---|---|
| `model` | JPA-сущности. `BaseEntity` (id), `Company`, `Client`, `Contact`, `Product`, `Deal`, `Task`, `Note`; enum `ClientType` и `DealStatus` |
| `model.user` | Пользователи: enum `UserRole`, абстрактный `User` и наследники `AdminUser`, `ManagerUser`, `CustomerUser` (одна таблица `users`, колонка `role` определяет класс) |
| `repository` | Интерфейсы Spring Data JPA, по одному на сущность, с методами поиска и фильтрами |
| `service` | Логика. `AbstractService` (общие операции) и по одному сервису на сущность |
| `controller` | REST-контроллеры, по одному на сущность |
| `dto` | Классы запросов (`ClientRequest`, `DealRequest` и т. д.) с проверками полей, а также `ProcessResponse` |
| `processing` | Многопоточная обработка сделок: `DealProcessor`, `ParallelDealProcessor` (ExecutorService), `NamedThreadFactory` (имена потоков) |

Вне Java-кода:

- `db/schema.sql` - создание таблиц (запускается в pgAdmin);
- `db/data.sql` - тестовые данные;
- `requests.http` - примеры запросов ко всем endpoint-ам (IntelliJ HTTP Client);
- `src/test` - unit-тесты.

## Схема базы данных

Восемь таблиц: `companies`, `clients`, `contacts`, `products`, `deals`, `users`, `tasks`, `notes`.
Схема построена по ER-модели из отчёта по БД. Отличие одно: у каждой таблицы суррогатный ключ `id BIGSERIAL`,
а бывшие естественные ключи (email, login, артикул, номер сделки, название компании) остаются `UNIQUE`.

| Связь | Что при удалении |
|---|---|
| компания 1:M клиент (`clients.company_id`, необязательна) | у клиентов `company_id` становится NULL |
| компания 1:M контакт (`contacts.company_id`) | контакты удаляются |
| клиент 1:M сделка, продукт 1:M сделка | удалить нельзя, пока есть сделки |
| сделка 1:M задача (`tasks.deal_id`, необязательна) | у задач `deal_id` становится NULL |
| сделка 1:M заметка | заметки удаляются |
| пользователь 1:M задача (`tasks.assignee_id`) | удалить нельзя, пока есть задачи |
| клиент 1:1 пользователь (`users.client_id`, только для роли CUSTOMER) | `client_id` становится NULL |

## Запуск

Нужны Java 17, Maven и PostgreSQL 16 (в pgAdmin или psql).

1. Создайте базу и пользователя (один раз):
   ```sql
   CREATE ROLE crm_user LOGIN PASSWORD 'ваш_пароль';
   CREATE DATABASE crm OWNER crm_user ENCODING 'UTF8';
   ```
2. Подключитесь к базе `crm` и выполните `db/schema.sql`, затем `db/data.sql`.
3. Укажите пароль: скопируйте `local.properties.example` в `local.properties` (файл в `.gitignore`) и впишите пароль.
   Либо задайте переменную окружения `DB_PASSWORD`. Адрес базы и пользователь лежат в `src/main/resources/application.properties`.
4. Запустите:
   ```
   mvn spring-boot:run
   ```
   В IntelliJ IDEA можно просто запустить класс `CrmApplication` (рабочая папка - корень проекта).
   Если IntelliJ предложит включить обработку аннотаций (Lombok), согласитесь.
5. Приложение работает на `http://localhost:8080`. Примеры запросов есть в `requests.http`.

Hibernate только проверяет, что таблицы совпадают с сущностями (`ddl-auto=validate`), сам он таблицы не создаёт.
Если русские буквы в консоли отображаются кракозябрами, добавьте VM options `-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8`.

## REST API

Для каждой сущности есть CRUD: `GET /api/{сущность}` (список), `GET /api/{сущность}/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`.
Коды ответа: 200 (ОК), 201 (создано), 204 (удалено), 400 (неверные данные или не найден связанный объект), 404 (объект не найден).

| Endpoint | Фильтры (необязательные) и дополнительные методы |
|---|---|
| `/api/companies` | `?city=` |
| `/api/clients` | `?name=` (часть имени), `?type=INDIVIDUAL\|COMPANY`, `?companyId=` |
| `/api/contacts` | `?companyId=` |
| `/api/products` | `?name=`, `?maxPrice=` |
| `/api/deals` | `?status=`, `?clientId=`, `?minAmount=`, `?maxAmount=`; `PATCH /{id}/status?status=`; `GET /total?status=` (сумма по статусу); `POST /process-new` (многопоточная обработка NEW) |
| `/api/tasks` | `?assigneeId=`, `?dealId=`, `?done=`, `?overdue=true`; `PATCH /{id}/done` |
| `/api/notes` | `?dealId=` |
| `/api/users` | `?role=ADMIN\|MANAGER\|CUSTOMER` |

Фильтры можно комбинировать, например `GET /api/deals?status=NEW&minAmount=10000`.

Что пока не реализовано (лабораторная №3): свои исключения и подробные сообщения об ошибках. Сейчас при нарушении
уникальности (повторный email, логин и т. д.) или при удалении объекта со связями Spring возвращает общий ответ 500.

## Тесты

```
mvn verify
```

Тесты используют `@SpringBootTest` и `@MockBean`: репозитории заменены заглушками Mockito, а автонастройка
БД отключена в `src/test/resources/application.properties`. Поэтому тесты не подключаются к базе (и работают в CI без PostgreSQL).

- `service` - логика сервисов (создание, обновление, поиск, связанные объекты, потоки);
- `controller` - REST через `MockMvc`: коды ответа, проверка данных, фильтры;
- `model` - проверка ограничений полей (`EntityValidationTest`) и ролей пользователей;
- `processing` - многопоточная обработка сделок.

## CI

`.gitlab-ci.yml` запускает `mvn verify` (сборка и unit-тесты) при каждом push.
