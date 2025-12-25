# Expense Tracker - Трекер Расходов

Веб-приложение для учета личных финансов на Java Spring Boot.

## Технологии

- Java 17
- Spring Boot 3.2.1
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL / H2
- Thymeleaf
- Maven

## Быстрый старт

### Требования

- JDK 17+
- Maven 3.8+ (или использовать Maven Wrapper)

### Запуск (режим разработки)

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Приложение будет доступно по адресу: http://localhost:8080

### H2 Console (для отладки БД)

http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:expensetracker`
- Username: `sa`
- Password: (пустой)

### Swagger UI (документация API)

http://localhost:8080/swagger-ui.html

## Структура проекта

```
src/
├── main/
│   ├── java/com/expensetracker/
│   │   ├── config/          # Конфигурация
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA Entities
│   │   ├── enums/           # Enumerations
│   │   ├── exception/       # Exception Handling
│   │   ├── mapper/          # MapStruct Mappers
│   │   ├── repository/      # Spring Data Repositories
│   │   ├── security/        # Security & JWT
│   │   ├── service/         # Business Logic
│   │   └── util/            # Utilities
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       ├── static/          # CSS, JS, images
│       └── templates/       # Thymeleaf templates
└── test/                    # Tests
```

## Разработка

Проект находится в стадии разработки.

### Текущий статус

- [x] Инициализация проекта
- [ ] Entities и Repositories
- [ ] Security + JWT
- [ ] REST API
- [ ] Frontend
- [ ] Тестирование

## Автор

Проектная работа по дисциплине "Разработка Web приложений на Java Spring"

## Дата

Декабрь 2025 - Март 2026
