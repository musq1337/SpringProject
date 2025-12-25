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
    
    private CategoryResponse category;
    
    private BigDecimal limitAmount;
    
    private BudgetPeriod period;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private BigDecimal spent;
    
    private BigDecimal remaining;
    
    private Double percentUsed;
    
    private String status;
    
    private Integer daysRemaining;
    
    private LocalDateTime createdAt;
}
