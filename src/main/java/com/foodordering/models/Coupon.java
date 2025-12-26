package com.foodordering.models;

/**
 * Represents a simple coupon that can apply a discount to a cart subtotal.
 */
public class Coupon {
    public enum Type { PERCENTAGE, FIXED }

    private final String code;
    private final Type type;
    private final double value; // percent (0-100] or fixed amount >= 0

    public Coupon(String code, Type type, double value) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Coupon code must not be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Coupon type must not be null");
        }
        if (type == Type.PERCENTAGE && (value <= 0 || value > 100)) {
            throw new IllegalArgumentException("Percentage coupon must be in (0, 100]");
        }
        if (type == Type.FIXED && value < 0) {
            throw new IllegalArgumentException("Fixed coupon must be >= 0");
        }
        this.code = code;
        this.type = type;
        this.value = value;
    }

    public String getCode() { return code; }
    public Type getType() { return type; }
    public double getValue() { return value; }

    /**
     * Calculate discount for a given subtotal.
     */
    public double computeDiscount(double subtotal) {
        if (subtotal <= 0) return 0.0;
        switch (type) {
            case PERCENTAGE:
                return subtotal * (value / 100.0);
            case FIXED:
                return Math.min(value, subtotal);
            default:
                return 0.0;
        }
    }
}
