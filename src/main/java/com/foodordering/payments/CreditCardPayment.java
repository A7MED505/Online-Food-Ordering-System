package com.foodordering.payments;

import com.foodordering.interfaces.Orderable;

/**
 * Simple credit card payment implementation for demonstration and testing.
 */
public class CreditCardPayment implements Orderable {
    private final String cardNumber;
    private final String holderName;
    private final String expiry; // MM/YY
    private final String cvv;

    public CreditCardPayment(String cardNumber, String holderName, String expiry, String cvv) {
        this.cardNumber = cardNumber;
        this.holderName = holderName;
        this.expiry = expiry;
        this.cvv = cvv;
    }

    private boolean isValidCard() {
        return cardNumber != null && cardNumber.replaceAll("\\s", "").length() >= 12
                && holderName != null && !holderName.isBlank()
                && expiry != null && !expiry.isBlank()
                && cvv != null && cvv.length() >= 3;
    }

    @Override
    public boolean process(double amount) {
        if (amount <= 0) return false;
        return isValidCard();
    }
}
