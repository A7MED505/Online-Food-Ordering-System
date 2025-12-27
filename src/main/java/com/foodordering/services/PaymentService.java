package com.foodordering.services;

import com.foodordering.interfaces.Orderable;

/**
 * Simple payment service that delegates to an Orderable implementation.
 */
public class PaymentService {
    /**
     * Processes payment using the provided method for the given amount.
     * Returns true if payment is successful.
     */
    public boolean process(Orderable method, double amount) {
        if (method == null || amount <= 0) return false;
        return method.process(amount);
    }
}
