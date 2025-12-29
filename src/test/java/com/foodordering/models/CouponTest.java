package com.foodordering.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Coupon model.
 */
public class CouponTest {

    private static Coupon validCoupon;
    private static Coupon expiredCoupon;

    @BeforeAll
    public static void setUp() {
        // Valid coupon: 20% discount, expires tomorrow
        validCoupon = new Coupon("SAVE20", Coupon.Type.PERCENTAGE, 20);
        validCoupon.setExpirationDate(LocalDate.now().plusDays(1));
        validCoupon.setActive(true);

        // Expired coupon: 15% discount, expired yesterday
        expiredCoupon = new Coupon("EXPIRED15", Coupon.Type.PERCENTAGE, 15);
        expiredCoupon.setExpirationDate(LocalDate.now().minusDays(1));
        expiredCoupon.setActive(true);
    }

    @Test
    @Order(1)
    @DisplayName("Test valid coupon with active status and future expiration")
    void testValidCoupon() {
        assertNotNull(validCoupon, "Valid coupon should not be null");
        assertEquals("SAVE20", validCoupon.getCode(), "Coupon code should match");
        assertEquals(20, validCoupon.getValue(), "Discount value should be 20%");
        assertEquals(Coupon.Type.PERCENTAGE, validCoupon.getType(), "Coupon type should be PERCENTAGE");
        assertTrue(validCoupon.isActive(), "Coupon should be active");
        assertTrue(validCoupon.isValid(), "Coupon should be valid (active and not expired)");
    }

    @Test
    @Order(2)
    @DisplayName("Test expired coupon is not valid regardless of active status")
    void testExpiredCoupon() {
        assertNotNull(expiredCoupon, "Expired coupon should not be null");
        assertEquals("EXPIRED15", expiredCoupon.getCode(), "Coupon code should match");
        assertEquals(15, expiredCoupon.getValue(), "Discount value should be 15%");
        assertTrue(expiredCoupon.isActive(), "Coupon is marked active");
        assertFalse(expiredCoupon.isValid(), "Coupon should be invalid (expired)");
    }

    @Test
    @Order(3)
    @DisplayName("Test apply discount to cart subtotal")
    void testApplyDiscount() {
        double subtotal = 100.0;

        // Valid coupon should apply discount
        double validDiscount = validCoupon.computeDiscount(subtotal);
        assertEquals(20.0, validDiscount, 0.01, "Valid coupon should apply 20% discount on $100");

        double afterDiscount = subtotal - validDiscount;
        assertEquals(80.0, afterDiscount, 0.01, "After 20% discount on $100 should be $80");

        // Expired coupon should NOT apply discount
        double expiredDiscount = expiredCoupon.computeDiscount(subtotal);
        assertEquals(0.0, expiredDiscount, 0.01, "Expired coupon should not apply any discount");
    }
}
