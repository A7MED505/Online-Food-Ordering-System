package com.foodordering.payments;

import com.foodordering.interfaces.Orderable;

/**
 * Cash payment always succeeds for positive amounts.
 */
public class CashPayment implements Orderable {
    private final String receiver;

    public CashPayment(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean process(double amount) {
        return receiver != null && !receiver.isBlank() && amount > 0;
    }
}
