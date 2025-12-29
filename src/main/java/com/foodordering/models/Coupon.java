package com.foodordering.models;

import java.time.LocalDate;

/**
 * Represents a simple coupon that can apply a discount to a cart subtotal.
 */
public class Coupon {
    public enum Type { PERCENTAGE, FIXED }

    private int couponId;
    private final String code;
    private final Type type;
    private final double value; // percent (0-100] or fixed amount >= 0
    private LocalDate expirationDate;
    private boolean active;

    public Coupon(String code, Type type, double value) {
        this(0, code, type, value, LocalDate.now().plusDays(30), true);
    }

    public Coupon(int couponId, String code, Type type, double value, LocalDate expirationDate, boolean active) {
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
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        this.couponId = couponId;
        this.code = code;
        this.type = type;
        this.value = value;
        this.expirationDate = expirationDate;
        this.active = active;
    }

    public int getCouponId() { return couponId; }
    public void setCouponId(int couponId) { this.couponId = couponId; }

    public String getCode() { return code; }
    public Type getType() { return type; }
    public double getValue() { return value; }
    
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) {
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        this.expirationDate = expirationDate;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isValid() {
        return active && !LocalDate.now().isAfter(expirationDate);
    }

    /**
     * Calculate discount for a given subtotal.
     */
    public double computeDiscount(double subtotal) {
        if (subtotal <= 0 || !isValid()) return 0.0;
        switch (type) {
            case PERCENTAGE:
                return subtotal * (value / 100.0);
            case FIXED:
                return Math.min(value, subtotal);
            default:
                return 0.0;
        }
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "couponId=" + couponId +
                ", code='" + code + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", expirationDate=" + expirationDate +
                ", active=" + active +
                '}';
    }
}
