package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import com.expensetracker.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * repository для работы с категориями
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * найти все категории пользователя
     */
    List<Category> findByUserId(Long userId);
    
    /**
     * найти категории пользователя по типу
     */
    List<Category> findByUserIdAndType(Long userId, TransactionType type);
    
    /**
     * найти категорию пользвателя по имени
     */
    Optional<Category> findByUserIdAndName(Long userId, String name);
    
    /**
     * проверить существование категории с таким именем у пользователя
     */
    boolean existsByUserIdAndName(Long userId, String name);
    
    /**
     * проверить существование категории по имени и пользователю
     */
    boolean existsByNameAndUserId(String name, Long userId);
    
    /**
     * найти предустановленые категории пользователя
     */
    List<Category> findByUserIdAndIsDefaultTrue(Long userId);
}
