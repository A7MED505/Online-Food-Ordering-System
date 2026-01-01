package com.foodordering.exceptions;

/**
 * Exception thrown when input validation fails.
 * Used for user input, form data, and business rule validation.
 */
public class ValidationException extends BaseApplicationException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
