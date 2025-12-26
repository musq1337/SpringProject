# Multi-stage build для Backend
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Копируем Maven wrapper и pom.xml
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Скачиваем зависимости (кэшируется если pom.xml не изменился)
RUN ./mvnw dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем JAR из builder
COPY --from=builder /app/target/expense-tracker-*.jar app.jar

# Открываем порт
EXPOSE 8080

# Настройки JVM для production
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
