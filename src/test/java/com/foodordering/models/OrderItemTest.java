package com.foodordering.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("Test order item creation")
    void testOrderItemCreation() {
        OrderItem item = new OrderItem(1, 100, 5, 3, 12.99);
        
        assertEquals(1, item.getOrderItemId());
        assertEquals(100, item.getOrderId());
        assertEquals(5, item.getItemId());
        assertEquals(3, item.getQuantity());
        assertEquals(12.99, item.getUnitPrice(), 0.001);
    }

    @Test
    @DisplayName("Test quantity update")
    void testQuantityUpdate() {
        OrderItem item = new OrderItem(1, 100, 5, 2, 10.00);
        assertEquals(2, item.getQuantity());
        
        item.setQuantity(5);
        assertEquals(5, item.getQuantity());
        
        item.setQuantity(1);
        assertEquals(1, item.getQuantity());
    }

    @Test
    @DisplayName("Test invalid quantity throws exception")
    void testInvalidQuantity() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(1, 100, 5, 0, 10.00));
        assertEquals("Quantity must be greater than 0", ex.getMessage());
        
        Exception ex2 = assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(1, 100, 5, -2, 10.00));
        assertEquals("Quantity must be greater than 0", ex2.getMessage());
    }

    @Test
    @DisplayName("Test invalid unit price throws exception")
    void testInvalidUnitPrice() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new OrderItem(1, 100, 5, 2, -5.00));
        assertEquals("Unit price cannot be negative", ex.getMessage());
    }

    @Test
    @DisplayName("Test subtotal calculation")
    void testSubtotal() {
        OrderItem item = new OrderItem(1, 100, 5, 4, 7.50);
        double subtotal = item.getSubtotal();
        assertEquals(30.0, subtotal, 0.01);
    }
}
