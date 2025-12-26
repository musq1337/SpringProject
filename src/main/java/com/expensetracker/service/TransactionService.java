package com.expensetracker.service;

import com.expensetracker.dto.request.TransactionRequest;
import com.expensetracker.dto.response.TransactionResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.enums.TransactionType;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * сервис для работы с транзакциями
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * создать новую транзакцию
     */
    public TransactionResponse createTransaction(TransactionRequest request) {
        User currentUser = getCurrentUser();
        
        // проверка категории
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("категория не найдена"));
        
        // проверка что категория принадлежит пользователю
        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("категория не принадлежит пользователю");
        }
        
        // проверка типа транзакции и категории
        if (!category.getType().equals(request.getType())) {
            throw new BadRequestException("тип транзакции не соответствует типу категории");
        }
        
        Transaction transaction = Transaction.builder()
                .user(currentUser)
                .category(category)
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate() != null ? request.getDate() : LocalDate.now())
                .build();
        
        if (request.getTags() != null) {
            transaction.getTags().addAll(request.getTags());
        }
        
        transaction = transactionRepository.save(transaction);
        
        return mapToResponse(transaction);
    }

    /**
     * обновить транзакцию
     */
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = getTransactionByIdAndCheckOwner(id);
        
        // проверка категории если изменилась
        if (!transaction.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("категория не найдена"));
            
            if (!newCategory.getUser().getId().equals(transaction.getUser().getId())) {
                throw new BadRequestException("категория не принадлежит пользователю");
            }
            
            if (!newCategory.getType().equals(request.getType())) {
                throw new BadRequestException("тип транзакции не соответствует типу категории");
            }
            
            transaction.setCategory(newCategory);
        }
        
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        
        transaction.getTags().clear();
        if (request.getTags() != null) {
            transaction.getTags().addAll(request.getTags());
        }
        
        transaction = transactionRepository.save(transaction);
        
        return mapToResponse(transaction);
    }

    /**
     * удалить транзакцию
     */
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionByIdAndCheckOwner(id);
        transactionRepository.delete(transaction);
    }

    /**
     * получить транзакцию по id
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = getTransactionByIdAndCheckOwner(id);
        return mapToResponse(transaction);
    }

    /**
     * получить все транзакции с фильтрами
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(
            TransactionType type,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        User currentUser = getCurrentUser();
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Transaction> transactions = transactionRepository.findByFilters(
                currentUser.getId(),
                type,
                categoryId,
                startDate,
                endDate,
                search,
                pageable
        );
        
        return transactions.map(this::mapToResponse);
    }

    /**
     * получить сумму по типу за период
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalByType(TransactionType type, LocalDate startDate, LocalDate endDate) {
        User currentUser = getCurrentUser();
        BigDecimal total = transactionRepository.sumByUserIdAndTypeAndDateBetween(
                currentUser.getId(),
                type,
                startDate,
                endDate
        );
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * получить баланс за период
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(LocalDate startDate, LocalDate endDate) {
        BigDecimal income = getTotalByType(TransactionType.INCOME, startDate, endDate);
        BigDecimal expense = getTotalByType(TransactionType.EXPENSE, startDate, endDate);
        return income.subtract(expense);
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
     * получить транзакцию и проверить владельца
     */
    private Transaction getTransactionByIdAndCheckOwner(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("транзакция не найдена"));
        
        User currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("нет прав на эту транзакцию");
        }
        
        return transaction;
    }

    /**
     * маппинг в response
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .categoryColor(transaction.getCategory().getColor())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .tags(transaction.getTags())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
