package com.foodordering.integration;

import com.foodordering.dao.*;
import com.foodordering.models.*;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for complete end-to-end user scenarios.
 * Each test is independent and creates its own test data.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration Tests - End-to-End User Flows")
public class IntegrationTest {

    /**
     * Scenario 1: Full user journey
     * Register → Login → Browse Restaurants → Add Items to Cart → Checkout
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("E2E: Complete user flow - Register to Checkout")
    void testCompleteUserFlow() {
        UserDAO userDAO = new UserDAO();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        MenuItemDAO menuItemDAO = new MenuItemDAO();
        OrderDAO orderDAO = new OrderDAO();
        
        String testUsername = "e2e_test_" + System.currentTimeMillis();
        String testEmail = testUsername + "@test.com";
        
        try {
            // Step 1: Register new user
            User newUser = new User(0, testUsername, testEmail, "password123");
            boolean registered = userDAO.registerUser(newUser);
            assertTrue(registered, "User should be registered successfully");
            int userId = newUser.getId();
            assertTrue(userId > 0, "User ID should be generated");
            
            // Step 2: Login with registered credentials
            User loggedInUser = userDAO.login(testUsername, "password123");
            assertNotNull(loggedInUser, "User should login successfully");
            assertEquals(testUsername, loggedInUser.getUsername(), "Username should match");
            assertEquals(testEmail, loggedInUser.getEmail(), "Email should match");
            
            // Step 3: Create customer profile
            int customerId = createCustomer(userId, "5551234567", "123 Test Street");
            assertTrue(customerId > 0, "Customer profile should be created");
            
            // Step 4: Browse restaurants
            List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
            assertNotNull(restaurants, "Restaurants list should not be null");
            
            // Step 5: Create test restaurant and menu
            Restaurant testRestaurant = new Restaurant(0, "E2E Test Restaurant", "Test Address", "5550000000", 4.5);
            restaurantDAO.addRestaurant(testRestaurant);
            int restaurantId = testRestaurant.getRestaurantId();
            
            MenuItem item = new MenuItem(0, restaurantId, "Burger", 12.99, "Delicious burger", true);
            menuItemDAO.addMenuItem(item);
            
            // Step 6: Create a cart and add items
            Cart cart = new Cart();
            cart.addItem(item, 2);
            assertEquals(1, cart.getItems().size(), "Cart should have 1 unique item");
            assertEquals(2, cart.getItems().get(0).getQuantity(), "Item quantity should be 2");
            
            // Step 7: Create and place an order
            double totalPrice = cart.calculateTotal();
            assertTrue(totalPrice > 0, "Cart total should be positive");
            
            com.foodordering.models.Order order = new com.foodordering.models.Order(0, customerId, restaurantId, totalPrice, "pending", null);
            for (CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem(0, 0, cartItem.getItemId(), cartItem.getQuantity(), cartItem.getUnitPrice());
                order.addItem(orderItem);
            }
            
            boolean orderCreated = orderDAO.createOrder(order);
            assertTrue(orderCreated, "Order should be created successfully");
            assertTrue(order.getOrderId() > 0, "Order ID should be generated");
            
            // Step 8: Verify order
            com.foodordering.models.Order retrievedOrder = orderDAO.getOrderById(order.getOrderId());
            assertNotNull(retrievedOrder, "Order should be retrievable");
            assertEquals("pending", retrievedOrder.getStatus(), "Order status should be pending");
            assertEquals(totalPrice, retrievedOrder.getTotalPrice(), "Order total should match");
            
            // Cleanup
            deleteOrder(order.getOrderId());
            deleteRestaurant(restaurantId);
            deleteCustomer(customerId);
            deleteUser(userId);
            
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Scenario 2: Invalid login attempts
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Edge Case: Invalid login attempts")
    void testInvalidLoginAttempts() {
        UserDAO userDAO = new UserDAO();
        
        // Wrong password for non-existent user
        User wrongPassword = userDAO.login("nonexistent_user", "wrongpassword");
        assertNull(wrongPassword, "Login with non-existent user and wrong password should fail");
    }

    /**
     * Scenario 3: Invalid registration
     */
    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Edge Case: Invalid registration inputs")
    void testInvalidRegistration() {
        // Test invalid email format
        assertThrows(IllegalArgumentException.class, () -> {
            new User(0, "validuser", "invalid-email", "password123");
        }, "Invalid email should throw exception");
        
        // Test short password
        assertThrows(IllegalArgumentException.class, () -> {
            new User(0, "validuser", "valid@email.com", "short");
        }, "Short password should throw exception");
        
        // Test empty username
        assertThrows(IllegalArgumentException.class, () -> {
            new User(0, "", "valid@email.com", "password123");
        }, "Empty username should throw exception");
    }

    /**
     * Scenario 4: Cart operations and validation
     */
    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Edge Case: Cart operations and validation")
    void testCartEdgeCases() {
        Cart cart = new Cart();
        
        // Empty cart total
        assertEquals(0.0, cart.calculateTotal(), "Empty cart total should be 0");
        assertEquals(0, cart.getItems().size(), "Empty cart should have no items");
        
        // Add and remove items
        MenuItem item = new MenuItem(1, 1, "Test Item", 10.50, "Description", true);
        cart.addItem(item, 1);
        assertEquals(10.50, cart.calculateTotal(), "Cart total should match item price");
        
        cart.removeItem(item.getItemId());
        assertEquals(0.0, cart.calculateTotal(), "Cart should be empty after removing item");
        
        // Add same item multiple times - quantity should increase
        cart.addItem(item, 1);
        cart.addItem(item, 1);
        assertEquals(1, cart.getItems().size(), "Should have 1 unique item");
        assertEquals(2, cart.getItems().get(0).getQuantity(), "Quantity should be 2");
    }

    /**
     * Scenario 5: Invalid order creation
     */
    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("Edge Case: Invalid order scenarios")
    void testInvalidOrderScenarios() {
        // Order with negative total
        assertThrows(IllegalArgumentException.class, () -> {
            new com.foodordering.models.Order(0, 1, 1, -50.0, "pending", null);
        }, "Negative total should throw exception");
        
        // Order with empty status
        assertThrows(IllegalArgumentException.class, () -> {
            new com.foodordering.models.Order(0, 1, 1, 50.0, "", null);
        }, "Empty status should throw exception");
    }

    /**
     * Scenario 6: Order status update workflow
     */
    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("E2E: Order status update workflow")
    void testOrderStatusUpdateFlow() {
        UserDAO userDAO = new UserDAO();
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        OrderDAO orderDAO = new OrderDAO();
        
        String testUsername = "order_test_" + System.currentTimeMillis();
        
        try {
            // Create user, customer, restaurant and order
            User user = new User(0, testUsername, testUsername + "@test.com", "password123");
            userDAO.registerUser(user);
            int userId = user.getId();
            
            int customerId = createCustomer(userId, "5551111111", "Order Test St");
            
            Restaurant restaurant = new Restaurant(0, "Order Status Test", "Test Addr", "5550000000", 4.0);
            restaurantDAO.addRestaurant(restaurant);
            int restaurantId = restaurant.getRestaurantId();
            
            // Create order
            com.foodordering.models.Order order = new com.foodordering.models.Order(0, customerId, restaurantId, 99.99, "pending", null);
            boolean created = orderDAO.createOrder(order);
            assertTrue(created, "Order should be created");
            int orderId = order.getOrderId();
            
            // Test status updates
            assertTrue(orderDAO.updateOrderStatus(orderId, "confirmed"), "Status update to confirmed should succeed");
            com.foodordering.models.Order updated = orderDAO.getOrderById(orderId);
            assertEquals("confirmed", updated.getStatus(), "Status should be confirmed");
            
            assertTrue(orderDAO.updateOrderStatus(orderId, "preparing"), "Status update to preparing should succeed");
            updated = orderDAO.getOrderById(orderId);
            assertEquals("preparing", updated.getStatus(), "Status should be preparing");
            
            assertTrue(orderDAO.updateOrderStatus(orderId, "delivered"), "Status update to delivered should succeed");
            updated = orderDAO.getOrderById(orderId);
            assertEquals("delivered", updated.getStatus(), "Status should be delivered");
            
            // Cleanup
            deleteOrder(orderId);
            deleteRestaurant(restaurantId);
            deleteCustomer(customerId);
            deleteUser(userId);
            
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Scenario 7: Browse and review basic flow
     */
    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("E2E: Browse restaurants basic flow")
    void testBrowseRestaurantsFlow() {
        RestaurantDAO restaurantDAO = new RestaurantDAO();
        MenuItemDAO menuItemDAO = new MenuItemDAO();
        
        // Get all restaurants
        List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
        assertNotNull(restaurants, "Restaurants list should not be null");
        
        // Create and verify a new restaurant
        Restaurant testRest = new Restaurant(0, "Browse Test " + System.currentTimeMillis(), "Addr", "5550000000", 3.5);
        boolean added = restaurantDAO.addRestaurant(testRest);
        assertTrue(added, "Restaurant should be created");
        
        int restId = testRest.getRestaurantId();
        assertTrue(restId > 0, "Restaurant should have valid ID");
        
        // Get restaurant by ID
        Restaurant retrieved = restaurantDAO.getRestaurantById(restId);
        assertNotNull(retrieved, "Restaurant should be retrievable");
        assertEquals(testRest.getName(), retrieved.getName(), "Restaurant names should match");
        
        // Add menu items
        MenuItem item1 = new MenuItem(0, restId, "Pasta", 14.99, "Italian pasta", true);
        MenuItem item2 = new MenuItem(0, restId, "Salad", 9.99, "Fresh salad", true);
        menuItemDAO.addMenuItem(item1);
        menuItemDAO.addMenuItem(item2);
        
        // Get menu items
        List<MenuItem> items = menuItemDAO.getMenuItemsByRestaurant(restId);
        assertTrue(items.size() >= 2, "Restaurant should have at least 2 items");
        
        // Cleanup
        deleteRestaurant(restId);
    }

    // ==================== Helper Methods ====================

    private static int createCustomer(int userId, String phone, String address) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO customers (user_id, phone, address) VALUES (?, ?, ?)",
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error creating customer: " + ex.getMessage());
        }
        return 0;
    }

    private static void deleteOrder(int orderId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM order_items WHERE order_id = " + orderId);
            stmt.executeUpdate("DELETE FROM orders WHERE order_id = " + orderId);
        } catch (SQLException ex) {
            System.err.println("Error deleting order: " + ex.getMessage());
        }
    }

    private static void deleteRestaurant(int restaurantId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM menu_items WHERE restaurant_id = " + restaurantId);
            stmt.executeUpdate("DELETE FROM restaurants WHERE restaurant_id = " + restaurantId);
        } catch (SQLException ex) {
            System.err.println("Error deleting restaurant: " + ex.getMessage());
        }
    }

    private static void deleteCustomer(int customerId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM customers WHERE customer_id = " + customerId);
        } catch (SQLException ex) {
            System.err.println("Error deleting customer: " + ex.getMessage());
        }
    }

    private static void deleteUser(int userId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM users WHERE user_id = " + userId);
        } catch (SQLException ex) {
            System.err.println("Error deleting user: " + ex.getMessage());
        }
    }
}
