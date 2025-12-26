package com.foodordering.payments;

import com.foodordering.interfaces.Orderable;

/**
 * Simple debit card payment implementation.
 */
public class DebitCardPayment implements Orderable {
    private final String cardNumber;
    private final String holderName;

    public DebitCardPayment(String cardNumber, String holderName) {
        this.cardNumber = cardNumber;
        this.holderName = holderName;
    }

    private boolean isValid() {
        return cardNumber != null && cardNumber.replaceAll("\\s", "").length() >= 10
                && holderName != null && !holderName.isBlank();
    }

    @Override
    public boolean process(double amount) {
        if (amount <= 0) return false;
        return isValid();
    }
}
