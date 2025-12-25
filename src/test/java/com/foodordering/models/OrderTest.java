package com.foodordering.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("Test order creation")
    void testOrderCreation() {
        Order order = new Order(0, 1, 100, 50.0, "pending", null);
        
        assertEquals(0, order.getOrderId());
        assertEquals(1, order.getCustomerId());
        assertEquals(100, order.getRestaurantId());
        assertEquals(50.0, order.getTotalPrice(), 0.001);
        assertEquals("pending", order.getStatus());
        assertNull(order.getCouponId());
    }

    @Test
    @DisplayName("Test calculate total from items")
    void testCalculateTotal() {
        Order order = new Order(0, 1, 100, 0.0, "pending", null);
        
        OrderItem item1 = new OrderItem(0, 0, 1, 2, 10.50); // 2 * 10.50 = 21.00
        OrderItem item2 = new OrderItem(0, 0, 2, 3, 8.99);  // 3 * 8.99 = 26.97
        
        order.addItem(item1);
        order.addItem(item2);
        
        double calculatedTotal = order.calculateTotal();
        assertEquals(47.97, calculatedTotal, 0.01);
    }

    @Test
    @DisplayName("Test order status update")
    void testOrderStatus() {
        Order order = new Order(0, 1, 100, 50.0, "pending", null);
        assertEquals("pending", order.getStatus());
        
        order.setStatus("confirmed");
        assertEquals("confirmed", order.getStatus());
        
        order.setStatus("delivered");
        assertEquals("delivered", order.getStatus());
    }

    @Test
    @DisplayName("Test invalid negative total throws exception")
    void testInvalidTotal() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(0, 1, 100, -10.0, "pending", null));
        assertEquals("Total price cannot be negative", ex.getMessage());
    }

    @Test
    @DisplayName("Test invalid empty status throws exception")
    void testInvalidStatus() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(0, 1, 100, 50.0, "", null));
        assertEquals("Status cannot be empty", ex.getMessage());
    }
}
