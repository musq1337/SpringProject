package com.expensetracker.dto.request;

import com.expensetracker.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * запрос для создания/обновления транзакции
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    @NotNull(message = "тип транзакции обязателен")
    private TransactionType type;
    
    @NotNull(message = "сумма обязательна")
    @Positive(message = "сумма должна быть положительной")
    private BigDecimal amount;
    
    @NotNull(message = "категория обязательна")
    private Long categoryId;
    
    @NotNull(message = "дата обязательна")
    private LocalDate date;
    
    private String description;
    
    private List<String> tags;
}
