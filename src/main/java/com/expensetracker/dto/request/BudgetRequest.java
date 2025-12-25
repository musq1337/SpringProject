package com.expensetracker.dto.request;

import com.expensetracker.enums.BudgetPeriod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * запрос для создания/обновления бюджета
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    
    @NotNull(message = "категория обязательна")
    private Long categoryId;
    
    @NotNull(message = "лимит суммы обязателен")
    @Positive(message = "лимит должен быть положительным")
    private BigDecimal limitAmount;
    
    @NotNull(message = "период обязателен")
    private BudgetPeriod period;
    
    @NotNull(message = "дата начала обязательна")
    private LocalDate startDate;
}
