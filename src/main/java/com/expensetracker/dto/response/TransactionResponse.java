package com.expensetracker.dto.response;

import com.expensetracker.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ответ с данными транзакции
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    
    private Long id;
    
    private Long categoryId;
    
    private String categoryName;
    
    private String categoryColor;
    
    private TransactionType type;
    
    private BigDecimal amount;
    
    private LocalDate date;
    
    private String description;
    
    private List<String> tags;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
