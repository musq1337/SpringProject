package com.expensetracker.dto.request;

import com.expensetracker.enums.Currency;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * запрос для регистрации пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "email обязателен")
    @Email(message = "неверный формат email")
    private String email;
    
    @NotBlank(message = "пароль обязателен")
    @Size(min = 8, message = "пароль должен быть минимум 8 символов")
    private String password;
    
    @NotBlank(message = "имя обязательно")
    private String firstName;
    
    @NotBlank(message = "фамилия обязательна")
    private String lastName;
    
    private Currency defaultCurrency = Currency.RUB;
}
