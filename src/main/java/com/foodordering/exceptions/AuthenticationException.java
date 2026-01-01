package com.foodordering.exceptions;

/**
 * Exception thrown when user authentication fails.
 * This includes invalid credentials or authentication issues.
 */
public class AuthenticationException extends BaseApplicationException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
