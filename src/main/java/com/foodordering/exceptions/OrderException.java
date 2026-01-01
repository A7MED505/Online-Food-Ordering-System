package com.foodordering.exceptions;

/**
 * Exception thrown when order operations fail.
 * This includes invalid order state, empty cart, unavailable items, etc.
 */
public class OrderException extends BaseApplicationException {
    
    public OrderException(String message) {
        super(message);
    }
    
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
