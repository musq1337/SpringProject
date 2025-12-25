package com.expensetracker.exception;

/**
 * исключение при неправильном запросе
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}
