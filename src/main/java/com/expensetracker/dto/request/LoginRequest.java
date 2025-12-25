package com.expensetracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * запрос для входа в систему
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "email обязателен")
    @Email(message = "неверный формат email")
    private String email;
    
    @NotBlank(message = "пароль обязателен")
    private String password;
}
