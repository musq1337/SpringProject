package com.expensetracker.exception;

/**
 * исключение когда ресурс не найден
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s не найден с %s : '%s'", resource, field, value));
    }
}
