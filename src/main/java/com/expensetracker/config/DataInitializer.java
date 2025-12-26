package com.expensetracker.config;

import com.expensetracker.entity.User;
import com.expensetracker.enums.Currency;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Создаем тестового пользователя если его нет
        if (userRepository.findByEmail("test@test.com").isEmpty()) {
            User testUser = new User();
            testUser.setEmail("test@test.com");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setFirstName("Тест");
            testUser.setLastName("Пользователь");
            testUser.setDefaultCurrency(Currency.KZT);
            testUser.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(testUser);
            log.info("Создан тестовый пользователь: test@test.com / test123");
        } else {
            log.info("Тестовый пользователь уже существует: test@test.com");
        }
    }
}
