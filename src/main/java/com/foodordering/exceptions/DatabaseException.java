package com.foodordering.exceptions;

/**
 * Exception thrown when database operations fail.
 * Wraps SQLExceptions with more meaningful messages.
 */
public class DatabaseException extends BaseApplicationException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
