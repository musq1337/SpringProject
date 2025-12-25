package com.expensetracker.dto.request;

import com.expensetracker.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * запрос для создания/обновления категории
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    @NotBlank(message = "название категории обязательно")
    private String name;
    
    @NotNull(message = "тип категории обязателен")
    private TransactionType type;
    
    private String color;
    
    private String icon;
}
