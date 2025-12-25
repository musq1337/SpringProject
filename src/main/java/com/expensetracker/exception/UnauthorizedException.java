package com.expensetracker.exception;

/**
 * исключение при ошибке аутентификации
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
