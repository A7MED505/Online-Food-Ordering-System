package com.foodordering.services;

import com.foodordering.interfaces.Orderable;

/**
 * Payment processing service using Strategy pattern.
 * Delegates payment to provided payment method implementation.
 */
public class PaymentService {
    /**
     * Processes payment for given amount.
     * @param method Payment method implementation
     * @param amount Amount to charge (must be > 0)
     * @return true if payment succeeded
     */
    public boolean process(Orderable method, double amount) {
        if (method == null || amount <= 0) return false;
        return method.process(amount);
    }
}
