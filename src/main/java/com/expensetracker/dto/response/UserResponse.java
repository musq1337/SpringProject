package com.expensetracker.dto.response;

import com.expensetracker.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ответ с данными пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    private Long id;
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private Currency defaultCurrency;
    
    private LocalDateTime createdAt;
}
