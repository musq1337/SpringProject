package com.expensetracker.dto.response;

import com.expensetracker.enums.BudgetPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ответ с данными бюджета
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {
    
    private Long id;
    
    private Long categoryId;
    
    private String categoryName;
    
    private String categoryColor;
    
    private BigDecimal limitAmount;
    
    private BigDecimal spent;
    
    private BigDecimal remaining;
    
    private BigDecimal percentUsed;
    
    private BudgetPeriod period;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Long daysLeft;
    
    private Boolean isExceeded;
    
    private LocalDateTime createdAt;
}
