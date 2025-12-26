package com.foodordering.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    @Test
    void testAddItem() {
        Cart cart = new Cart();
        MenuItem pizza = new MenuItem(1, 1, "Pizza", 10.0, "Tasty", true);
        cart.addItem(pizza, 2);
        assertEquals(20.0, cart.calculateTotal());
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void testRemoveItem() {
        Cart cart = new Cart();
        MenuItem pizza = new MenuItem(1, 1, "Pizza", 10.0, "Tasty", true);
        MenuItem burger = new MenuItem(2, 1, "Burger", 7.5, "Juicy", true);
        cart.addItem(pizza, 2); // 20
        cart.addItem(burger, 1); // +7.5 => 27.5
        assertTrue(cart.removeItem(2));
        assertEquals(20.0, cart.calculateTotal());
    }

    @Test
    void testUpdateQuantity() {
        Cart cart = new Cart();
        MenuItem pizza = new MenuItem(1, 1, "Pizza", 10.0, "Tasty", true);
        cart.addItem(pizza, 1);
        cart.updateQuantity(1, 3);
        assertEquals(30.0, cart.calculateTotal());
    }

    @Test
    void testCalculateTotal() {
        Cart cart = new Cart();
        MenuItem pizza = new MenuItem(1, 1, "Pizza", 9.99, "Tasty", true);
        MenuItem soda = new MenuItem(2, 1, "Soda", 1.50, "Cold", true);
        cart.addItem(pizza, 2); // 19.98
        cart.addItem(soda, 3);  // +4.50 => 24.48
        assertEquals(24.48, cart.calculateTotal());
    }

    @Test
    void testClearCart() {
        Cart cart = new Cart();
        MenuItem pizza = new MenuItem(1, 1, "Pizza", 10.0, "Tasty", true);
        cart.addItem(pizza, 2);
        cart.clearCart();
        assertEquals(0.0, cart.calculateTotal());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testApplyCoupon() {
        Cart cart = new Cart();
        MenuItem pizza = new MenuItem(1, 1, "Pizza", 10.0, "Tasty", true);
        cart.addItem(pizza, 3); // total 30

        Coupon percent10 = new Coupon("SAVE10", Coupon.Type.PERCENTAGE, 10);
        cart.applyCoupon(percent10);
        assertEquals(27.00, cart.calculateTotal());

        Coupon fixed5 = new Coupon("MINUS5", Coupon.Type.FIXED, 5);
        cart.applyCoupon(fixed5);
        assertEquals(25.00, cart.calculateTotal());
    }
}
