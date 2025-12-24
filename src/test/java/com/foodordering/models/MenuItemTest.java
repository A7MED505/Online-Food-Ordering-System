package com.foodordering.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MenuItem class.
 * Tests menu item creation, validation, and toString functionality.
 */
class MenuItemTest {

    @Test
    @DisplayName("Test valid menu item creation")
    void testValidMenuItemCreation() {
        MenuItem item = new MenuItem(1, 100, "Margherita Pizza", 12.99, "Classic pizza with tomato and mozzarella", true);
        
        assertEquals(1, item.getItemId());
        assertEquals(100, item.getRestaurantId());
        assertEquals("Margherita Pizza", item.getName());
        assertEquals(12.99, item.getPrice(), 0.001);
        assertEquals("Classic pizza with tomato and mozzarella", item.getDescription());
        assertTrue(item.isAvailable());
    }

    @Test
    @DisplayName("Test menu item with invalid price throws exception")
    void testInvalidPrice() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new MenuItem(1, 100, "Invalid Item", -5.00, "Description", true);
        });
        
        assertEquals("Price must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("Test menu item with empty name throws exception")
    void testEmptyName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new MenuItem(1, 100, "", 10.00, "Description", true);
        });
        
        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Test menu item with null name throws exception")
    void testNullName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new MenuItem(1, 100, null, 10.00, "Description", true);
        });
        
        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Test menu item toString method")
    void testToString() {
        MenuItem item = new MenuItem(1, 100, "Burger", 8.50, "Delicious burger", true);
        String result = item.toString();
        
        assertTrue(result.contains("Burger"));
        assertTrue(result.contains("8.50"));
        assertTrue(result.contains("Available"));
    }

    @Test
    @DisplayName("Test setAvailable method")
    void testSetAvailable() {
        MenuItem item = new MenuItem(1, 100, "Pizza", 10.00, "Test pizza", true);
        assertTrue(item.isAvailable());
        
        item.setAvailable(false);
        assertFalse(item.isAvailable());
    }

    @Test
    @DisplayName("Test setPrice method with valid price")
    void testSetValidPrice() {
        MenuItem item = new MenuItem(1, 100, "Pasta", 15.00, "Delicious pasta", true);
        
        item.setPrice(18.50);
        assertEquals(18.50, item.getPrice(), 0.001);
    }

    @Test
    @DisplayName("Test setPrice method with invalid price")
    void testSetInvalidPrice() {
        MenuItem item = new MenuItem(1, 100, "Salad", 7.00, "Fresh salad", true);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            item.setPrice(-10.00);
        });
        
        assertEquals("Price must be greater than or equal to 0", exception.getMessage());
    }
}
