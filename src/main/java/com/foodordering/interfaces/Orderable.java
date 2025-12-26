package com.foodordering.interfaces;

/**
 * Represents a generic payment operation that can process an amount.
 * Implementations should validate inputs and return true when the
 * payment is accepted and processed, false otherwise.
 */
public interface Orderable {
    /**
     * Process a payment for the given amount.
     *
     * @param amount payment amount, must be positive
     * @return true if processed successfully, false otherwise
     */
    boolean process(double amount);
}
