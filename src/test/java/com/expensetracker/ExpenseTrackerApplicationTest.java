package com.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * основной интеграционный тест
 */
@SpringBootTest
@ActiveProfiles("test")
class ExpenseTrackerApplicationTest {

    @Test
    void contextLoads() {
        // Проверка что Spring контекст загружается без ошибок
    }
}
