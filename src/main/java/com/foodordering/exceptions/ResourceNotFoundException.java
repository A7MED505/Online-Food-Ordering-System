package com.foodordering.exceptions;

/**
 * Exception thrown when a requested resource (user, restaurant, order, etc.) 
 * is not found in the database.
 */
public class ResourceNotFoundException extends BaseApplicationException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
