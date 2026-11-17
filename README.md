# CRM system

Учебный проект (курсовая работа по Java): клиент-серверная CRM-система на Spring Boot + Maven.
Проект развивается по лабораторным работам, каждая лабораторная - отдельная ветка (`lab3` - текущая).

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
| `exception` | Собственные исключения CRM, преобразование ограничений PostgreSQL и `GlobalExceptionHandler` (`@RestControllerAdvice`) |
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
Если русские буквы в консоли не отображаются, добавьте VM options `-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8`.

## REST API

Для каждой сущности есть CRUD: `GET /api/{сущность}` (список), `GET /api/{сущность}/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`.
Коды ответа: 200 (ОК), 201 (создано), 204 (удалено), 400 (неверные данные или не найден связанный объект), 404 (объект не найден), 409 (конфликт данных), 500/503 (ошибка сервера/недоступность БД).

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

## Лабораторная работа №3: обработка исключений

Операции создания, обновления, изменения статуса и удаления либо завершаются успешно, либо выбрасывают
конкретное исключение. `Optional` остается для обычного поиска `findById`, а `getById` и `requireReference`
сообщают об отсутствии запрошенного или связанного объекта. Контроллеры передают ошибки в
`GlobalExceptionHandler`: у разных типов ошибок отдельные `@ExceptionHandler`.

| Исключение / ситуация | HTTP | Код ошибки |
|---|---|---|
| `EntityNotFoundException`: объект по ID не найден | 404 | `ENTITY_NOT_FOUND` |
| `InvalidReferenceException`: указан ID отсутствующего связанного объекта | 400 | `INVALID_REFERENCE` |
| `BusinessRuleViolationException`: смена роли, clientId у не-CUSTOMER, неверный диапазон сумм | 400 | `BUSINESS_RULE_VIOLATION` |
| `DuplicateResourceException`: занято уникальное значение | 409 | `DUPLICATE_RESOURCE` |
| `ResourceInUseException`: удаление клиента/продукта со сделками или пользователя с задачами | 409 | `RESOURCE_IN_USE` |
| `DataConflictException`: другое нарушение целостности | 409 | `DATA_CONFLICT` |
| Bean Validation: неверный email, пустое имя, отрицательная сумма и т. п. | 400 | `VALIDATION_ERROR` |
| Пустое тело, неверный JSON, тип поля, дата или enum в теле | 400 | `INVALID_JSON` |
| Неверный тип параметра пути/запроса | 400 | `INVALID_PARAMETER` |
| Пропущен обязательный параметр запроса | 400 | `MISSING_PARAMETER` |
| `DealProcessingException`: сбой или прерывание обработки сделок | 500 | `DEAL_PROCESSING_FAILED` |
| База данных недоступна | 503 | `DATABASE_UNAVAILABLE` |
| Другая ошибка доступа к данным | 500 | `DATABASE_ERROR` |
| Непредвиденная ошибка | 500 | `INTERNAL_ERROR` |

Для ошибок протокола сохраняются HTTP-статусы и заголовки Spring (например, 405 и `Allow`), тело имеет тот же формат.
Резервный обработчик 500 используется после отдельных обработчиков известных ошибок. Подробности неожиданных
ошибок пишутся в серверный лог; SQL, значения из исключений драйвера и stack trace не возвращаются клиенту.

Пример запроса `GET /api/clients/999999`, если такого клиента нет:

```json
{
  "timestamp": "2026-11-18T12:00:00+03:00",
  "status": 404,
  "code": "ENTITY_NOT_FOUND",
  "message": "Клиент с id=999999 не найден(а).",
  "path": "/api/clients/999999",
  "suggestion": "Проверьте идентификатор или запросите список объектов.",
  "fieldErrors": {}
}
```

При ошибке проверки полей `fieldErrors` содержит списки сообщений по именам полей, например
`{"email": ["Неверный формат email"]}`. Клиент может ориентироваться на стабильный `code`,
а пользователь — на `message` и `suggestion`.

Перед сохранением проверяется уникальность названия компании, email клиента и контакта, артикула,
номера сделки, логина и привязки клиента к аккаунту CUSTOMER. Обновление допускает сохранение собственного
уникального значения. Ограничения PostgreSQL остаются окончательной защитой при одновременных запросах:
`DatabaseExceptionTranslator` различает SQLSTATE и известные имена ограничений из `db/schema.sql`.
При исчезновении связанного объекта между проверкой и сохранением возвращается 400 с рекомендацией обновить ID.
Неизвестные ограничения получают безопасный общий ответ 409.

Каскадное удаление и обнуление ссылок из ЛР2 сохраняются. Запрещённое удаление проверяется сервисом,
а нарушение внешнего ключа при конкурентном запросе также преобразуется в 409.

`ParallelDealProcessor` передает сбои рабочих потоков через `DealProcessingException`, сохраняет исходную
причину, восстанавливает флаг прерывания и завершает оставшиеся задачи. `DealService` вызывает `saveAll`
только после успешного завершения всех задач; при сбое API не сообщает об успешной обработке.

Примеры успешных и ошибочных запросов находятся в `requests.http`.

## Тесты

```
mvn verify
```

Тесты используют `@SpringBootTest` и `@MockBean`: репозитории заменены заглушками Mockito, а автонастройка
БД отключена в `src/test/resources/application.properties`. Поэтому тесты не подключаются к базе (и работают в CI без PostgreSQL).

- `service` - логика сервисов (создание, обновление, поиск, связанные объекты, потоки);
- `controller` - REST через `MockMvc`: коды ответа, проверка данных, фильтры;
- `model` - проверка ограничений полей (`EntityValidationTest`) и ролей пользователей;
- `processing` - многопоточная обработка сделок, сбой потока и прерывание ожидания;
- `ErrorHandlingTest` - JSON-контракт ошибок через MockMvc, уникальность, связи, ошибки БД и потоков;
- `DatabaseExceptionTranslatorTest` - SQLSTATE и имена ограничений без подключения к БД;
- `ServiceFailureTest` - уникальность при обновлении и отсутствие изменений при отклонении запроса.

## CI

На GitHub workflow `.github/workflows/maven.yml` запускает `mvn verify` на Java 17 при push и открытии/обновлении
Pull Request. PostgreSQL и секреты для тестов не нужны. Результаты находятся во вкладке Actions и в проверках PR.
Файл `.gitlab-ci.yml` сохранен для запуска тех же проверок при использовании GitLab.

Работа по ЛР3 ведется в ветке `lab3`. Pull Request в `master` остается открытым до проверки преподавателем;
слияние выполняется после его одобрения.
