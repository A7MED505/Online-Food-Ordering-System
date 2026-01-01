package com.foodordering.exceptions;

/**
 * Exception thrown when attempting to register a user with 
 * an already existing username or email.
 */
public class UserAlreadyExistsException extends BaseApplicationException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
