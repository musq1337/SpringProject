package com.expensetracker.repository;

import com.expensetracker.entity.Transaction;
import com.expensetracker.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * repository для работы с транзакциями
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * найти все транзакции пользователя с пагинацией
     */
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    /**
     * найти транзакции пользователя по типу
     */
    Page<Transaction> findByUserIdAndType(Long userId, TransactionType type, Pageable pageable);
    
    /**
     * найти транзакции пользователя по категори
     */
    Page<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);
    
    /**
     * найти транзакции пользователя за период
     */
    Page<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * найти транзакции пользователя по типу за период
     */
    List<Transaction> findByUserIdAndTypeAndDateBetween(
        Long userId, TransactionType type, LocalDate startDate, LocalDate endDate
    );
    
    /**
     * найти транзакции пользователя по категории за период
     */
    List<Transaction> findByUserIdAndCategoryIdAndDateBetween(
        Long userId, Long categoryId, LocalDate startDate, LocalDate endDate
    );
    
    /**
     * посчитать общую сумму транзакций пользователя по типу за период
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndTypeAndDateBetween(
        @Param("userId") Long userId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * посчитать сумму транзакций по категории за период
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.category.id = :categoryId " +
           "AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndCategoryIdAndDateBetween(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * получить суммы расходов по категориям за перид
     */
    @Query("SELECT t.category.id, SUM(t.amount) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.category.id")
    List<Object[]> sumExpensesByCategoryGrouped(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * получить сумму расходов по категории за период
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.category.id = :categoryId AND t.type = 'EXPENSE' " +
           "AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumExpensesByCategoryAndDateBetween(
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * посчитать общую сумму транзакций пользователя по типу за все время
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumByUserIdAndType(
        @Param("userId") Long userId,
        @Param("type") TransactionType type
    );
    
    /**
     * подсчет количества транзакций по категории
     */
    long countByCategoryId(Long categoryId);
    
    /**
     * получить расходы по категориям с группировкой
     */
    @Query("SELECT c.name, c.color, SUM(t.amount) FROM Transaction t " +
           "JOIN t.category c WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY c.id, c.name, c.color ORDER BY SUM(t.amount) DESC")
    List<Object[]> findExpensesByCategoryGrouped(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * найти транзакции с фильтрами
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Transaction> findByFilters(
        @Param("userId") Long userId,
        @Param("type") TransactionType type,
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("search") String search,
        Pageable pageable
    );
}
