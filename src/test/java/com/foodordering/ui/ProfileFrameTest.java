package com.foodordering.ui;

import com.foodordering.dao.OrderDAO;
import com.foodordering.dao.RestaurantDAO;
import com.foodordering.dao.UserDAO;
import com.foodordering.models.Order;
import com.foodordering.models.User;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileFrameTest {

    private static UserDAO userDAO;
    private static OrderDAO orderDAO;
    private static RestaurantDAO restaurantDAO;
    private static int testUserId;
    private static int testCustomerId;
    private static int testRestaurantId;
    private static int testOrderId;
    private static String uniqueSuffix;
    private static String testRestaurantName;

    @BeforeAll
    public static void setUp() {
        userDAO = new UserDAO();
        orderDAO = new OrderDAO();
        restaurantDAO = new RestaurantDAO();

        uniqueSuffix = String.valueOf(System.currentTimeMillis());
        testRestaurantName = "Test Restaurant " + uniqueSuffix;

        // Register test user
        User testUser = new User(0, "profiletest_" + uniqueSuffix, "profiletest_" + uniqueSuffix + "@test.com", "password123");
        boolean registered = userDAO.registerUser(testUser);
        assertTrue(registered, "Test user should be registered");
        testUserId = testUser.getId();
        assertTrue(testUserId > 0, "Test user ID should be retrieved");

        // Create customer record
        testCustomerId = createCustomer(testUserId, "1234567890", "123 Test Street");
        assertTrue(testCustomerId > 0, "Test customer should be created");

        // Create test restaurant
        com.foodordering.models.Restaurant restaurant = new com.foodordering.models.Restaurant(
            0, testRestaurantName, "Test Address", "9876543210", 0.0);
        boolean restaurantAdded = restaurantDAO.addRestaurant(restaurant);
        assertTrue(restaurantAdded, "Test restaurant should be created");
        testRestaurantId = restaurant.getRestaurantId();
        assertTrue(testRestaurantId > 0, "Test restaurant ID should be retrieved");

        // Create test order
        Order order = new Order(0, testCustomerId, testRestaurantId, 50.0, "delivered", null);
        boolean orderCreated = orderDAO.createOrder(order);
        assertTrue(orderCreated, "Test order should be created");
        testOrderId = order.getOrderId(); 
        assertTrue(testOrderId > 0, "Test order ID should be set");
    }

    @AfterAll
    public static void tearDown() {
        // Clean up in reverse order
        if (testOrderId > 0) {
            deleteOrder(testOrderId);
        }
        if (testRestaurantId > 0) {
            deleteRestaurant(testRestaurantId);
        }
        if (testCustomerId > 0) {
            deleteCustomer(testCustomerId);
        }
        if (testUserId > 0) {
            deleteUser(testUserId);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Test get order history displays orders correctly")
    void testGetOrderHistory() {
        // Create a simple test user for this test
        User testUser = new User(testUserId, "profiletest", "profiletest@test.com", "password123");
        
        // Create ProfileFrame
        ProfileFrame frame = new ProfileFrame(testUser, userDAO, orderDAO);
        assertNotNull(frame, "ProfileFrame should be created");

        // Verify order history loaded
        int rowCount = frame.getOrderHistoryRowCount();
        assertTrue(rowCount >= 1, "Order history should contain at least 1 order");

        // Verify order data in table
        var tableModel = frame.getOrderTableModel();
        boolean foundOrder = false;
        for (int i = 0; i < rowCount; i++) {
            int orderId = (int) tableModel.getValueAt(i, 0);
            if (orderId == testOrderId) {
                foundOrder = true;
                String restaurant = (String) tableModel.getValueAt(i, 1);
                String total = (String) tableModel.getValueAt(i, 2);
                String status = (String) tableModel.getValueAt(i, 3);

                assertEquals(testRestaurantName, restaurant, "Restaurant name should match");
                assertEquals("$50.00", total, "Order total should match");
                assertEquals("delivered", status, "Order status should match");
                break;
            }
        }
        assertTrue(foundOrder, "Test order should be found in order history");

        frame.dispose();
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Test update profile updates customer information")
    void testUpdateProfile() {
        // Create a simple test user for this test
        User testUser = new User(testUserId, "profiletest", "profiletest@test.com", "password123");

        // Create ProfileFrame
        ProfileFrame frame = new ProfileFrame(testUser, userDAO, orderDAO);
        assertNotNull(frame, "ProfileFrame should be created");

        // Verify initial values loaded
        String initialPhone = frame.getPhoneText();
        String initialAddress = frame.getAddressText();
        assertEquals("1234567890", initialPhone, "Initial phone should match");
        assertEquals("123 Test Street", initialAddress, "Initial address should match");

        // Update profile programmatically
        updateCustomerInfo(testCustomerId, "0987654321", "456 New Avenue");

        // Reload profile
        ProfileFrame frame2 = new ProfileFrame(testUser, userDAO, orderDAO);
        String updatedPhone = frame2.getPhoneText();
        String updatedAddress = frame2.getAddressText();

        assertEquals("0987654321", updatedPhone, "Updated phone should match");
        assertEquals("456 New Avenue", updatedAddress, "Updated address should match");

        frame.dispose();
        frame2.dispose();
    }

    // Helper methods
    private static int createCustomer(int userId, String phone, String address) {
        String sql = "INSERT INTO customers (user_id, phone, address) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

    private static void deleteCustomer(int customerId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM customers WHERE customer_id = " + customerId);
        } catch (SQLException ex) {
            System.err.println("Error deleting customer: " + ex.getMessage());
        }
    }

    private static void updateCustomerInfo(int customerId, String phone, String address) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = String.format(
                    "UPDATE customers SET phone = '%s', address = '%s' WHERE customer_id = %d",
                    phone, address, customerId);
            stmt.executeUpdate(sql);
        } catch (SQLException ex) {
            System.err.println("Error updating customer: " + ex.getMessage());
        }
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
        restaurantDAO.deleteRestaurant(restaurantId);
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
