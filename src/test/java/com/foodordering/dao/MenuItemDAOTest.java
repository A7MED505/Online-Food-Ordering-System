package com.foodordering.dao;

import com.foodordering.models.MenuItem;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for MenuItemDAO.
 * Tests CRUD operations for menu items.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MenuItemDAOTest {

    private static MenuItemDAO menuItemDAO;
    private static int testRestaurantId;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        menuItemDAO = new MenuItemDAO();
        
        // Create a test restaurant first
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO restaurants (name, address, phone, rating) VALUES ('Test Restaurant', '123 Test St', '555-0001', 4.5)");
            var rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) {
                testRestaurantId = rs.getInt(1);
            }
            rs.close();
        }
    }

    @AfterEach
    void cleanUpAfterEach() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM menu_items WHERE restaurant_id = " + testRestaurantId);
        }
    }

    @AfterAll
    static void cleanUpDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM menu_items WHERE restaurant_id = " + testRestaurantId);
            stmt.execute("DELETE FROM restaurants WHERE restaurant_id = " + testRestaurantId);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test adding menu item")
    void testAddMenuItem() {
        MenuItem item = new MenuItem(0, testRestaurantId, "Test Pizza", 12.99, "Delicious test pizza", true);
        boolean result = menuItemDAO.addMenuItem(item);
        
        assertTrue(result, "Menu item should be added successfully");
        assertTrue(item.getItemId() > 0, "Item ID should be set after insertion");
    }

    @Test
    @Order(2)
    @DisplayName("Test getting menu item by ID")
    void testGetMenuItemById() {
        MenuItem item = new MenuItem(0, testRestaurantId, "Test Burger", 8.50, "Juicy burger", true);
        menuItemDAO.addMenuItem(item);
        
        MenuItem retrieved = menuItemDAO.getMenuItemById(item.getItemId());
        
        assertNotNull(retrieved, "Menu item should be found");
        assertEquals("Test Burger", retrieved.getName());
        assertEquals(8.50, retrieved.getPrice(), 0.001);
        assertEquals(testRestaurantId, retrieved.getRestaurantId());
    }

    @Test
    @Order(3)
    @DisplayName("Test getting menu items by restaurant ID")
    void testGetMenuItemsByRestaurant() {
        MenuItem item1 = new MenuItem(0, testRestaurantId, "Pizza", 10.00, "Pizza desc", true);
        MenuItem item2 = new MenuItem(0, testRestaurantId, "Pasta", 12.00, "Pasta desc", true);
        
        menuItemDAO.addMenuItem(item1);
        menuItemDAO.addMenuItem(item2);
        
        List<MenuItem> items = menuItemDAO.getMenuItemsByRestaurant(testRestaurantId);
        
        assertNotNull(items, "Items list should not be null");
        assertTrue(items.size() >= 2, "Should have at least 2 items");
    }

    @Test
    @Order(4)
    @DisplayName("Test updating menu item")
    void testUpdateMenuItem() {
        MenuItem item = new MenuItem(0, testRestaurantId, "Original Name", 15.00, "Original desc", true);
        menuItemDAO.addMenuItem(item);
        
        item.setName("Updated Name");
        item.setPrice(18.50);
        item.setAvailable(false);
        
        boolean result = menuItemDAO.updateMenuItem(item);
        
        assertTrue(result, "Update should succeed");
        
        MenuItem updated = menuItemDAO.getMenuItemById(item.getItemId());
        assertEquals("Updated Name", updated.getName());
        assertEquals(18.50, updated.getPrice(), 0.001);
        assertFalse(updated.isAvailable());
    }

    @Test
    @Order(5)
    @DisplayName("Test deleting menu item")
    void testDeleteMenuItem() {
        MenuItem item = new MenuItem(0, testRestaurantId, "To Delete", 5.00, "Will be deleted", true);
        menuItemDAO.addMenuItem(item);
        int itemId = item.getItemId();
        
        boolean result = menuItemDAO.deleteMenuItem(itemId);
        
        assertTrue(result, "Delete should succeed");
        
        MenuItem deleted = menuItemDAO.getMenuItemById(itemId);
        assertNull(deleted, "Deleted item should not be found");
    }

    @Test
    @Order(6)
    @DisplayName("Test getting non-existent menu item returns null")
    void testGetNonExistentMenuItem() {
        MenuItem item = menuItemDAO.getMenuItemById(99999);
        assertNull(item, "Non-existent item should return null");
    }
}
