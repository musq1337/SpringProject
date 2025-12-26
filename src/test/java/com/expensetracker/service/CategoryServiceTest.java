package com.expensetracker.service;

import com.expensetracker.dto.request.CategoryRequest;
import com.expensetracker.dto.response.CategoryResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Currency;
import com.expensetracker.enums.TransactionType;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * тесты для CategoryService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .defaultCurrency(Currency.KZT)
                .build();

        category = Category.builder()
                .id(1L)
                .name("Продукты")
                .type(TransactionType.EXPENSE)
                .color("#FF0000")
                .icon("cart")
                .isDefault(false)
                .user(user)
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Продукты");
        categoryRequest.setType(TransactionType.EXPENSE);
        categoryRequest.setColor("#FF0000");
        categoryRequest.setIcon("cart");

        // Mock Security Context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    }

    @Test
    void createCategory_Success() {
        // Arrange
        when(categoryRepository.existsByNameAndUserId(anyString(), anyLong())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse response = categoryService.createCategory(categoryRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Продукты", response.getName());
        assertEquals(TransactionType.EXPENSE, response.getType());
        assertEquals("#FF0000", response.getColor());

        verify(categoryRepository).existsByNameAndUserId("Продукты", 1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        // Arrange
        when(categoryRepository.existsByNameAndUserId(anyString(), anyLong())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> categoryService.createCategory(categoryRequest));
        
        verify(categoryRepository).existsByNameAndUserId("Продукты", 1L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getAllCategories_Success() {
        // Arrange
        Category category2 = Category.builder()
                .id(2L)
                .name("Транспорт")
                .type(TransactionType.EXPENSE)
                .user(user)
                .build();

        when(categoryRepository.findByUserId(1L)).thenReturn(Arrays.asList(category, category2));

        // Act
        List<CategoryResponse> responses = categoryService.getAllCategories(null);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(categoryRepository).findByUserId(1L);
    }

    @Test
    void getAllCategories_FilterByType_Success() {
        // Arrange
        when(categoryRepository.findByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(Arrays.asList(category));

        // Act
        List<CategoryResponse> responses = categoryService.getAllCategories(TransactionType.EXPENSE);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(TransactionType.EXPENSE, responses.get(0).getType());
        verify(categoryRepository).findByUserIdAndType(1L, TransactionType.EXPENSE);
    }

    @Test
    void deleteCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.countByCategoryId(1L)).thenReturn(0L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).findById(1L);
        verify(transactionRepository).countByCategoryId(1L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_HasTransactions_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.countByCategoryId(1L)).thenReturn(5L);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(1L));
        
        verify(transactionRepository).countByCategoryId(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
        
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_NotOwner_ThrowsException() {
        // Arrange
        User anotherUser = User.builder().id(2L).email("another@test.com").build();
        category.setUser(anotherUser);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(1L));
        
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
