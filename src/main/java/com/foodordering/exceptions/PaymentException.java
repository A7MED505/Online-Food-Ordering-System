package com.foodordering.exceptions;

/**
 * Exception thrown when payment processing fails.
 * This includes insufficient funds, invalid payment methods, etc.
 */
public class PaymentException extends BaseApplicationException {
    
    public PaymentException(String message) {
        super(message);
    }
    
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
