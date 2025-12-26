# 🐳 Docker Deployment Guide

## Быстрый старт

### 1. Сборка и запуск всех сервисов
```bash
docker-compose up -d --build
```

### 2. Открыть приложение
Откройте браузер: **http://localhost**

**Тестовый пользователь:**
- Email: `test@test.com`
- Пароль: `test123`

---

## Управление контейнерами

### Просмотр логов
```bash
# Все сервисы
docker-compose logs -f

# Только backend
docker-compose logs -f backend

# Только frontend
docker-compose logs -f frontend
```

### Остановка
```bash
docker-compose down
```

### Остановка с удалением данных
```bash
docker-compose down -v
```

### Перезапуск
```bash
docker-compose restart
```

### Пересборка после изменений
```bash
# Backend
docker-compose up -d --build backend

# Frontend
docker-compose up -d --build frontend

# Все
docker-compose up -d --build
```

---

## Порты

- **Frontend**: http://localhost (порт 80)
- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

---

## Проверка состояния

```bash
# Статус контейнеров
docker-compose ps

# Health check backend
curl http://localhost:8080/actuator/health
```

---

## Производственное развертывание

### 1. Измените настройки безопасности

В `docker-compose.yml`:
```yaml
environment:
  # Установите надежный секрет (минимум 32 символа)
  JWT_SECRET: ваш-супер-секретный-ключ-здесь
```

### 2. Используйте внешнюю базу данных (опционально)

Раскомментируйте PostgreSQL в `docker-compose.yml` и измените настройки backend:
```yaml
backend:
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/expensetracker
    SPRING_DATASOURCE_USERNAME: postgres
    SPRING_DATASOURCE_PASSWORD: postgres123
```

### 3. Настройте reverse proxy (Nginx/Traefik)

Для HTTPS и доменного имени используйте Nginx или Traefik перед контейнерами.

---

## Бэкапы

### Бэкап данных H2
```bash
# Скопировать файл БД из контейнера
docker cp expensetracker-backend:/data/expensetracker.mv.db ./backup.mv.db
```

### Восстановление
```bash
# Скопировать обратно в контейнер
docker cp ./backup.mv.db expensetracker-backend:/data/expensetracker.mv.db
docker-compose restart backend
```

---

## Troubleshooting

### Backend не запускается
```bash
# Проверить логи
docker-compose logs backend

# Проверить health check
docker inspect expensetracker-backend | grep -A 10 Health
```

### Frontend показывает ошибки API
Убедитесь что backend запущен и доступен:
```bash
curl http://localhost:8080/actuator/health
```

### Порты заняты
Измените порты в `docker-compose.yml`:
```yaml
ports:
  - "3000:80"  # Frontend на порту 3000
  - "8081:8080"  # Backend на порту 8081
```

---

## Системные требования

- **Docker**: >= 20.10
- **Docker Compose**: >= 2.0
- **RAM**: >= 2GB (рекомендуется 4GB)
- **Disk**: >= 2GB свободного места

---

## Архитектура

```
┌─────────────┐
│   Browser   │
└──────┬──────┘
       │ http://localhost
       ▼
┌─────────────────┐
│   Frontend      │ (Nginx + React)
│   Port 80       │
└────────┬────────┘
         │ http://backend:8080/api
         ▼
┌─────────────────┐
│   Backend       │ (Spring Boot)
│   Port 8080     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   H2 Database   │ (File-based)
│   /data/        │
└─────────────────┘
```

---

## Дополнительные команды

### Очистка Docker
```bash
# Удалить неиспользуемые образы
docker image prune -a

# Удалить все (осторожно!)
docker system prune -a --volumes
```

### Экспорт образов
```bash
# Сохранить образы в файлы
docker save expensetracker-backend -o backend.tar
docker save expensetracker-frontend -o frontend.tar

# Загрузить на другом ПК
docker load -i backend.tar
docker load -i frontend.tar
```
