# Expense Tracker - Система учета расходов

Полнофункциональное веб-приложение для учета личных финансов с возможностью отслеживания доходов, расходов, управления бюджетами и аналитикой. Проектная работа студента **Saduakhas Muslim** по дисциплине "Разработка Web приложений на Java Spring".

## 🚀 Технологии

### Backend
- **Java 21** (Zulu OpenJDK)
- **Spring Boot 3.2.1**
- **Spring Security** с JWT аутентификацией
- **Spring Data JPA** + Hibernate
- **H2 Database** (in-memory для разработки)
- **PostgreSQL** (production ready)
- **Maven** для сборки
- **JUnit 5 + Mockito** для тестирования

### Frontend
- **React 18** + **TypeScript**
- **Vite** (build tool)
- **Tailwind CSS** для стилизации
- **React Router** для навигации
- **TanStack Query** для работы с API
- **React Hook Form** для форм
- **Axios** для HTTP запросов
- **Recharts** для графиков

## 📋 Требования

- **Java 21** (Zulu OpenJDK или любой другой)
- **Node.js 18+** и **npm**
- **Git**

## 🔧 Установка и запуск

### 1. Клонирование репозитория

```bash
git clone <repository-url>
cd JavaSpringProject
```

### 2. Запуск Backend

#### Windows PowerShell:

```powershell
# Установка JAVA_HOME (если требуется)
$env:JAVA_HOME = "C:\Program Files\Zulu\zulu-21"

# Запуск приложения
.\mvnw spring-boot:run
```

#### Linux/MacOS:

```bash
./mvnw spring-boot:run
```

Backend будет доступен на **http://localhost:8080**

### 3. Запуск Frontend

Откройте новый терминал:

```bash
cd front
npm install
npm run dev
```

Frontend будет доступен на **http://localhost:5173**

## 🧪 Запуск тестов

### Backend тесты

```bash
.\mvnw test
```

Все 38 тестов должны пройти успешно.

### Frontend сборка

```bash
cd front
npm run build
```

## 📊 Структура проекта

```
JavaSpringProject/
├── src/main/java/com/expensetracker/
│   ├── config/           # Конфигурация (Security, OpenAPI)
│   ├── controller/       # REST контроллеры
│   ├── dto/              # Data Transfer Objects
│   │   ├── request/      # Request DTOs
│   │   └── response/     # Response DTOs
│   ├── entity/           # JPA сущности
│   ├── enums/            # Перечисления
│   ├── exception/        # Обработка исключений
│   ├── repository/       # Spring Data репозитории
│   ├── security/         # JWT и аутентификация
│   ├── service/          # Бизнес-логика
│   └── util/             # Утилиты
├── src/main/resources/
│   ├── application.properties          # Конфигурация приложения
│   └── application-prod.properties     # Production конфигурация
├── src/test/             # Тесты (38 тестов)
├── front/                # React приложение
│   ├── src/
│   │   ├── components/   # React компоненты
│   │   ├── pages/        # Страницы
│   │   ├── services/     # API сервисы
│   │   ├── store/        # State management
│   │   ├── types/        # TypeScript типы
│   │   └── utils/        # Утилиты
│   └── public/           # Статические файлы
├── target/               # Скомпилированные файлы
├── pom.xml               # Maven конфигурация
└── README.md             # Этот файл
```

## 🔑 Основные функции

### ✅ Реализовано

- ✅ **Регистрация и аутентификация** пользователей (JWT)
- ✅ **CRUD операции** для транзакций (доходы/расходы)
- ✅ **Категории**: 10 категорий по умолчанию (6 расходов + 4 дохода)
- ✅ **Бюджеты**: установка лимитов по категориям
- ✅ **Аналитика**: статистика расходов, доходов, баланс
- ✅ **Мультивалютность**: KZT (₸), USD ($), EUR (€)
- ✅ **Графики**: визуализация расходов по категориям
- ✅ **Полное покрытие тестами**: 38 тестов backend

### 🎯 API Endpoints

#### Аутентификация
- `POST /api/auth/register` - Регистрация
- `POST /api/auth/login` - Вход
- `POST /api/auth/refresh` - Обновление токена

#### Транзакции
- `GET /api/transactions` - Список транзакций
- `POST /api/transactions` - Создание транзакции
- `GET /api/transactions/{id}` - Получение транзакции
- `PUT /api/transactions/{id}` - Обновление транзакции
- `DELETE /api/transactions/{id}` - Удаление транзакции

#### Категории
- `GET /api/categories` - Список категорий (автосоздание по умолчанию)
- `POST /api/categories` - Создание категории

#### Бюджеты
- `GET /api/budgets` - Список бюджетов
- `POST /api/budgets` - Создание бюджета
- `GET /api/budgets/{id}` - Получение бюджета
- `PUT /api/budgets/{id}` - Обновление бюджета
- `DELETE /api/budgets/{id}` - Удаление бюджета

#### Аналитика
- `GET /api/analytics/dashboard` - Дашборд с общей статистикой

## 🗄️ База данных

### Режим разработки (по умолчанию)
- **H2 In-Memory Database**
- URL: `jdbc:h2:mem:expensetracker`
- Console: http://localhost:8080/h2-console
- Username: `sa`
- Password: (оставить пустым)

### Production режим
Для использования PostgreSQL создайте `application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expensetracker
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

Запуск с production профилем:
```bash
.\mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## 🔧 Конфигурация

### Backend (application.properties)
- **Server Port**: 8080
- **JWT Access Token**: 24 часа
- **JWT Refresh Token**: 7 дней
- **CORS**: разрешен для http://localhost:5173
- **Database**: H2 in-memory

### Frontend
- **Dev Server**: http://localhost:5173
- **API Base URL**: http://localhost:8080/api
- **Auth Storage**: localStorage

## 🐛 Устранение неполадок

### Backend не запускается
- Проверьте версию Java: `java -version` (должна быть 21)
- Убедитесь что порт 8080 свободен
- Проверьте переменную `JAVA_HOME`

### Frontend не подключается к Backend
- Убедитесь что backend запущен на порту 8080
- Проверьте настройки CORS в `SecurityConfig.java`
- Откройте DevTools браузера и проверьте консоль

### Ошибки CORS
- Backend настроен для работы с `http://localhost:5173`
- Если меняете порт frontend, обновите `SecurityConfig.java`

### Тесты не проходят
```bash
# Очистите и пересоберите проект
.\mvnw clean install
```

## 📦 Сборка для Production

### Backend JAR
```bash
.\mvnw clean package
```
JAR файл будет создан в `target/expense-tracker-1.0.0.jar`

### Frontend Production Build
```bash
cd front
npm run build
```
Статические файлы будут в `front/dist/`

## 📝 Автор

**Saduakhas Muslim**  
Проектная работа по дисциплине "Разработка Web приложений на Java Spring"  
Декабрь 2025

## 📄 Лицензия

Учебный проект 
