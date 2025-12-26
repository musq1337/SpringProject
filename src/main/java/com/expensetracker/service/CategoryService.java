package com.expensetracker.service;

import com.expensetracker.dto.request.CategoryRequest;
import com.expensetracker.dto.response.CategoryResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.enums.TransactionType;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис для работы с категориями
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * создать новую категорию
     */
    public CategoryResponse createCategory(CategoryRequest request) {
        User currentUser = getCurrentUser();
        
        // проверка уникальности имени
        if (categoryRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new BadRequestException("категория с таким именем уже существует");
        }
        
        Category category = Category.builder()
                .user(currentUser)
                .name(request.getName())
                .type(request.getType())
                .color(request.getColor())
                .icon(request.getIcon())
                .isDefault(false)
                .build();
        
        category = categoryRepository.save(category);
        
        return mapToResponse(category);
    }

    /**
     * обновить категорию
     */
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = getCategoryByIdAndCheckOwner(id);
        
        // проверка уникальности имени если оно изменилось
        if (!category.getName().equals(request.getName())) {
            if (categoryRepository.existsByNameAndUserId(request.getName(), category.getUser().getId())) {
                throw new BadRequestException("категория с таким именем уже существует");
            }
        }
        
        category.setName(request.getName());
        category.setType(request.getType());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        
        category = categoryRepository.save(category);
        
        return mapToResponse(category);
    }

    /**
     * удалить категорию
     */
    public void deleteCategory(Long id) {
        Category category = getCategoryByIdAndCheckOwner(id);
        
        // проверка что нет транзакций с этой категорией
        long transactionCount = transactionRepository.countByCategoryId(id);
        if (transactionCount > 0) {
            throw new BadRequestException("невозможно удалить категорию с существующими транзакциями");
        }
        
        categoryRepository.delete(category);
    }

    /**
     * получить категорию по id
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = getCategoryByIdAndCheckOwner(id);
        return mapToResponse(category);
    }

    /**
     * получить все категории пользователя
     */
    public List<CategoryResponse> getAllCategories(TransactionType type) {
        User currentUser = getCurrentUser();
        
        List<Category> categories;
        if (type != null) {
            categories = categoryRepository.findByUserIdAndType(currentUser.getId(), type);
        } else {
            categories = categoryRepository.findByUserId(currentUser.getId());
        }
        
        // если у пользователя нет категорий, создаем дефолтные
        if (categories.isEmpty()) {
            createDefaultCategories(currentUser);
            if (type != null) {
                categories = categoryRepository.findByUserIdAndType(currentUser.getId(), type);
            } else {
                categories = categoryRepository.findByUserId(currentUser.getId());
            }
        }
        
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * создать дефолтные категории для нового пользователя
     */
    private void createDefaultCategories(User user) {
        // Категории расходов
        createDefaultCategory(user, "🍔 Еда", TransactionType.EXPENSE, "#ef4444");
        createDefaultCategory(user, "🚗 Транспорт", TransactionType.EXPENSE, "#f97316");
        createDefaultCategory(user, "🏠 Дом", TransactionType.EXPENSE, "#3b82f6");
        createDefaultCategory(user, "🎮 Развлечения", TransactionType.EXPENSE, "#8b5cf6");
        createDefaultCategory(user, "🏥 Здоровье", TransactionType.EXPENSE, "#10b981");
        createDefaultCategory(user, "🛍️ Покупки", TransactionType.EXPENSE, "#ec4899");
        
        // Категории доходов
        createDefaultCategory(user, "💼 Зарплата", TransactionType.INCOME, "#22c55e");
        createDefaultCategory(user, "💰 Подработка", TransactionType.INCOME, "#14b8a6");
        createDefaultCategory(user, "🎁 Подарки", TransactionType.INCOME, "#a855f7");
        createDefaultCategory(user, "📈 Инвестиции", TransactionType.INCOME, "#06b6d4");
    }

    /**
     * создать одну дефолтную категорию
     */
    private void createDefaultCategory(User user, String name, TransactionType type, String color) {
        Category category = Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .color(color)
                .isDefault(true)
                .build();
        categoryRepository.save(category);
    }

    /**
     * получить текущего пользователя
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("пользователь не найден"));
    }

    /**
     * получить категорию и проверить владельца
     */
    private Category getCategoryByIdAndCheckOwner(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("категория не найдена"));
        
        User currentUser = getCurrentUser();
        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("нет прав на эту категорию");
        }
        
        return category;
    }

    /**
     * маппинг в response
     */
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .color(category.getColor())
                .icon(category.getIcon())
                .isDefault(category.getIsDefault())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
