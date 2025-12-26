package com.expensetracker.repository;

import com.expensetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * repository для работы с бюджетами
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    /**
     * найти все бюджеты пользователя
     */
    List<Budget> findByUserId(Long userId);
    
    /**
     * найти активные бюджеты пользователя (на текущую дату)
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND :currentDate BETWEEN b.startDate AND b.endDate")
    List<Budget> findActiveByUserId(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);
    
    /**
     * найти бюджет пользователя по категори (активный)
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.category.id = :categoryId " +
           "AND :currentDate BETWEEN b.startDate AND b.endDate")
    Optional<Budget> findActiveByUserIdAndCategoryId(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("currentDate") LocalDate currentDate
    );
    
    /**
     * проверить существование активного бюджета для категории
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Budget b " +
           "WHERE b.user.id = :userId AND b.category.id = :categoryId " +
           "AND :currentDate BETWEEN b.startDate AND b.endDate")
    boolean existsActiveByUserIdAndCategoryId(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("currentDate") LocalDate currentDate
    );
    
    /**
     * проверка существования бюджета
     */
    boolean existsByUserIdAndCategoryIdAndStartDate(Long userId, Long categoryId, LocalDate startDate);
}
