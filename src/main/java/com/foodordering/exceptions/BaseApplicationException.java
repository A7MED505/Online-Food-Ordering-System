package com.foodordering.exceptions;

/**
 * Base exception class for all custom application exceptions.
 * Provides common functionality for exception hierarchy.
 */
public class BaseApplicationException extends Exception {
    
    public BaseApplicationException(String message) {
        super(message);
    }
    
    public BaseApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
