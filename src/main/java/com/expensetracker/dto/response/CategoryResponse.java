package com.expensetracker.dto.response;

import com.expensetracker.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ответ с данными категории
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    
    private Long id;
    
    private String name;
    
    private TransactionType type;
    
    private String color;
    
    private String icon;
    
    private Boolean isDefault;
    
    private LocalDateTime createdAt;
    
    private Long transactionCount;
}
