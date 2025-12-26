package com.expensetracker.service;

import com.expensetracker.dto.request.BudgetRequest;
import com.expensetracker.dto.response.BudgetResponse;
import com.expensetracker.entity.Budget;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.enums.BudgetPeriod;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * сервис для работы с бюджетами
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * создать новый бюджет
     */
    public BudgetResponse createBudget(BudgetRequest request) {
        User currentUser = getCurrentUser();
        
        // проверка категории
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("категория не найдена"));
        
        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("категория не принадлежит пользователю");
        }
        
        // вычисление дат
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        LocalDate endDate = calculateEndDate(startDate, request.getPeriod());
        
        // проверка что нет пересечений
        if (budgetRepository.existsByUserIdAndCategoryIdAndStartDate(
                currentUser.getId(), category.getId(), startDate)) {
            throw new BadRequestException("бюджет для этой категории уже существует на этот период");
        }
        
        Budget budget = Budget.builder()
                .user(currentUser)
                .category(category)
                .limitAmount(request.getLimitAmount())
                .period(request.getPeriod())
                .startDate(startDate)
                .endDate(endDate)
                .build();
        
        budget = budgetRepository.save(budget);
        
        return mapToResponse(budget);
    }

    /**
     * обновить бюджет
     */
    public BudgetResponse updateBudget(Long id, BudgetRequest request) {
        Budget budget = getBudgetByIdAndCheckOwner(id);
        
        // проверка категории если изменилась
        if (!budget.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("категория не найдена"));
            
            if (!newCategory.getUser().getId().equals(budget.getUser().getId())) {
                throw new BadRequestException("категория не принадлежит пользователю");
            }
            
            budget.setCategory(newCategory);
        }
        
        budget.setLimitAmount(request.getLimitAmount());
        budget.setPeriod(request.getPeriod());
        
        if (request.getStartDate() != null) {
            budget.setStartDate(request.getStartDate());
            budget.setEndDate(calculateEndDate(request.getStartDate(), request.getPeriod()));
        }
        
        budget = budgetRepository.save(budget);
        
        return mapToResponse(budget);
    }

    /**
     * удалить бюджет
     */
    public void deleteBudget(Long id) {
        Budget budget = getBudgetByIdAndCheckOwner(id);
        budgetRepository.delete(budget);
    }

    /**
     * получить бюджет по id
     */
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long id) {
        Budget budget = getBudgetByIdAndCheckOwner(id);
        return mapToResponse(budget);
    }

    /**
     * получить все активные бюджеты пользователя
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllActiveBudgets() {
        User currentUser = getCurrentUser();
        LocalDate now = LocalDate.now();
        
        List<Budget> budgets = budgetRepository.findActiveByUserId(currentUser.getId(), now);
        
        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * получить все бюджеты пользователя
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets() {
        User currentUser = getCurrentUser();
        
        List<Budget> budgets = budgetRepository.findByUserId(currentUser.getId());
        
        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * вычислить дату окончания бюджета
     */
    private LocalDate calculateEndDate(LocalDate startDate, BudgetPeriod period) {
        return switch (period) {
            case MONTHLY -> startDate.plusMonths(1).minusDays(1);
            case YEARLY -> startDate.plusYears(1).minusDays(1);
        };
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
     * получить бюджет и проверить владельца
     */
    private Budget getBudgetByIdAndCheckOwner(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("бюджет не найден"));
        
        User currentUser = getCurrentUser();
        if (!budget.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("нет прав на этот бюджет");
        }
        
        return budget;
    }

    /**
     * маппинг в response с расчетом статуса
     */
    private BudgetResponse mapToResponse(Budget budget) {
        // получить сумму расходов по категории за период
        BigDecimal spent = transactionRepository.sumExpensesByCategoryAndDateBetween(
                budget.getCategory().getId(),
                budget.getStartDate(),
                budget.getEndDate()
        );
        
        if (spent == null) {
            spent = BigDecimal.ZERO;
        }
        
        // вычисление процента использования
        BigDecimal percentUsed = BigDecimal.ZERO;
        if (budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentUsed = spent.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        // оставшаяся сумма
        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        
        // дней осталось
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), budget.getEndDate());
        if (daysLeft < 0) {
            daysLeft = 0;
        }
        
        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .categoryColor(budget.getCategory().getColor())
                .limitAmount(budget.getLimitAmount())
                .spent(spent)
                .remaining(remaining)
                .percentUsed(percentUsed)
                .period(budget.getPeriod())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .daysLeft(daysLeft)
                .isExceeded(spent.compareTo(budget.getLimitAmount()) > 0)
                .createdAt(budget.getCreatedAt())
                .build();
    }
}
