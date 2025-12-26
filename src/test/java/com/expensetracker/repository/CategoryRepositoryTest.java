package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Currency;
import com.expensetracker.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * тесты для CategoryRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .defaultCurrency(Currency.KZT)
                .build();
        user = entityManager.persistAndFlush(user);
    }

    @Test
    void findByUserId_ReturnsAllUserCategories() {
        // Arrange
        Category category1 = createCategory("Продукты", TransactionType.EXPENSE);
        Category category2 = createCategory("Зарплата", TransactionType.INCOME);
        entityManager.persistAndFlush(category1);
        entityManager.persistAndFlush(category2);

        // Act
        List<Category> categories = categoryRepository.findByUserId(user.getId());

        // Assert
        assertEquals(2, categories.size());
    }

    @Test
    void findByUserIdAndType_ReturnsFilteredCategories() {
        // Arrange
        Category expense = createCategory("Продукты", TransactionType.EXPENSE);
        Category income = createCategory("Зарплата", TransactionType.INCOME);
        entityManager.persistAndFlush(expense);
        entityManager.persistAndFlush(income);

        // Act
        List<Category> expenseCategories = categoryRepository.findByUserIdAndType(
                user.getId(), TransactionType.EXPENSE);

        // Assert
        assertEquals(1, expenseCategories.size());
        assertEquals("Продукты", expenseCategories.get(0).getName());
    }

    @Test
    void existsByNameAndUserId_ReturnsTrue_WhenExists() {
        // Arrange
        Category category = createCategory("Продукты", TransactionType.EXPENSE);
        entityManager.persistAndFlush(category);

        // Act
        boolean exists = categoryRepository.existsByNameAndUserId("Продукты", user.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByNameAndUserId_ReturnsFalse_WhenNotExists() {
        // Act
        boolean exists = categoryRepository.existsByNameAndUserId("Несуществующая", user.getId());

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByUserIdAndName_ReturnsCategory() {
        // Arrange
        Category category = createCategory("Продукты", TransactionType.EXPENSE);
        entityManager.persistAndFlush(category);

        // Act
        Optional<Category> found = categoryRepository.findByUserIdAndName(user.getId(), "Продукты");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Продукты", found.get().getName());
    }

    @Test
    void save_CreatesNewCategory() {
        // Arrange
        Category category = createCategory("Новая", TransactionType.EXPENSE);

        // Act
        Category saved = categoryRepository.save(category);

        // Assert
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Новая", saved.getName());
    }

    @Test
    void delete_RemovesCategory() {
        // Arrange
        Category category = createCategory("Удалить", TransactionType.EXPENSE);
        category = entityManager.persistAndFlush(category);
        Long categoryId = category.getId();

        // Act
        categoryRepository.delete(category);
        entityManager.flush();

        // Assert
        Optional<Category> deleted = categoryRepository.findById(categoryId);
        assertFalse(deleted.isPresent());
    }

    private Category createCategory(String name, TransactionType type) {
        return Category.builder()
                .name(name)
                .type(type)
                .color("#FF0000")
                .icon("icon")
                .isDefault(false)
                .user(user)
                .build();
    }
}
