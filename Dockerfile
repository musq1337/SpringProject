# Multi-stage build для Backend
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Копируем Maven wrapper и pom.xml
COPY .mvn .mvn
COPY mvnw mvnw.cmd pom.xml ./

# Исправляем права и конвертируем mvnw для Unix
RUN chmod +x mvnw && \
    dos2unix mvnw || sed -i 's/\r$//' mvnw || true

# Копируем исходный код (делаем это раньше чтобы кэшировать зависимости вместе)
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Создаем директорию для данных и пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring && \
    mkdir -p /data && \
    chown -R spring:spring /data /app

# Копируем JAR из builder
COPY --from=builder --chown=spring:spring /app/target/expense-tracker-*.jar app.jar

# Переключаемся на пользователя spring
USER spring:spring

# Открываем порт
EXPOSE 8080

# Настройки JVM для production
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
