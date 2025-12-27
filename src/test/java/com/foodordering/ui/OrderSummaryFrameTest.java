package com.foodordering.ui;

import com.foodordering.dao.MenuItemDAO;
import com.foodordering.dao.OrderDAO;
import com.foodordering.dao.RestaurantDAO;
import com.foodordering.dao.UserDAO;
import com.foodordering.models.*;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for OrderSummaryFrame UI.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderSummaryFrameTest {

    private static OrderDAO orderDAO;
    private static UserDAO userDAO;
    private static RestaurantDAO restaurantDAO;
    private static MenuItemDAO menuItemDAO;

    private static int testUserId;
    private static int testCustomerId;
    private static int testRestaurantId;
    private static int testMenuItem1Id;
    private static int testMenuItem2Id;
    private static int testOrderId;

    @BeforeAll
    static void setup() throws SQLException {
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        restaurantDAO = new RestaurantDAO();
        menuItemDAO = new MenuItemDAO();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        // Create test user and customer
        User testUser = new User(0, "summary_test_user", "summary@test.com", "TestPass123");
        userDAO.registerUser(testUser);
        testUserId = testUser.getId();

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO customers (user_id, address, phone) VALUES (" + testUserId + ", '789 Summary St', '555-9999')");
            var rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) {
                testCustomerId = rs.getInt(1);
            }
            rs.close();
        }

        // Create test restaurant
        Restaurant testRestaurant = new Restaurant(0, "Summary Test Restaurant", "999 Test Blvd", "555-0000", 4.8);
        restaurantDAO.addRestaurant(testRestaurant);
        testRestaurantId = testRestaurant.getRestaurantId();

        // Create test menu items
        MenuItem item1 = new MenuItem(0, testRestaurantId, "Summary Pizza", 18.99, "Test pizza", true);
        MenuItem item2 = new MenuItem(0, testRestaurantId, "Summary Pasta", 14.50, "Test pasta", true);
        menuItemDAO.addMenuItem(item1);
        menuItemDAO.addMenuItem(item2);
        testMenuItem1Id = item1.getItemId();
        testMenuItem2Id = item2.getItemId();

        // Create test order
        com.foodordering.models.Order testOrder = new com.foodordering.models.Order(0, testCustomerId, testRestaurantId, 0.0, "pending", null);
        OrderItem orderItem1 = new OrderItem(0, 0, testMenuItem1Id, 2, 18.99);
        OrderItem orderItem2 = new OrderItem(0, 0, testMenuItem2Id, 1, 14.50);
        testOrder.addItem(orderItem1);
        testOrder.addItem(orderItem2);
        double calculatedTotal = testOrder.calculateTotal();
        testOrder.setTotalPrice(calculatedTotal);

        boolean created = orderDAO.createOrder(testOrder);
        assertTrue(created, "Test order should be created");
        testOrderId = testOrder.getOrderId();
    }

    @AfterAll
    static void teardown() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM order_items WHERE order_id IN (SELECT order_id FROM orders WHERE customer_id = " + testCustomerId + ")");
            stmt.execute("DELETE FROM orders WHERE customer_id = " + testCustomerId);
            stmt.execute("DELETE FROM menu_items WHERE restaurant_id = " + testRestaurantId);
            stmt.execute("DELETE FROM restaurants WHERE restaurant_id = " + testRestaurantId);
            stmt.execute("DELETE FROM customers WHERE customer_id = " + testCustomerId);
            stmt.execute("DELETE FROM users WHERE user_id = " + testUserId);
        }
        DatabaseConnection.getInstance().closeConnection();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Test display order details in OrderSummaryFrame")
    void testDisplayOrderDetails() {
        // Retrieve order from DB
        com.foodordering.models.Order order = orderDAO.getOrderById(testOrderId);
        assertNotNull(order, "Order should exist");

        // Create OrderSummaryFrame
        OrderSummaryFrame frame = new OrderSummaryFrame(order, orderDAO);
        assertNotNull(frame, "Frame should be created");

        // Verify order ID label
        String orderIdText = frame.getOrderIdText();
        assertTrue(orderIdText.contains(String.valueOf(testOrderId)), "Order ID should be displayed");

        // Verify status label
        String statusText = frame.getStatusText();
        assertTrue(statusText.toLowerCase().contains("pending"), "Status should be displayed");

        // Verify total label
        String totalText = frame.getTotalText();
        assertTrue(totalText.contains("52.48"), "Total should display calculated amount");

        // Verify items table
        assertEquals(2, frame.getTableModel().getRowCount(), "Should display 2 items");

        frame.dispose();
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Test order confirmation message in OrderSummaryFrame")
    void testOrderConfirmation() {
        // Retrieve order from DB
        com.foodordering.models.Order order = orderDAO.getOrderById(testOrderId);
        assertNotNull(order, "Order should exist");

        // Create OrderSummaryFrame
        OrderSummaryFrame frame = new OrderSummaryFrame(order, orderDAO);

        // Verify confirmation message
        String confirmationText = frame.getConfirmationText();
        assertNotNull(confirmationText, "Confirmation text should not be null");
        assertTrue(confirmationText.contains("Thank you"), "Confirmation should contain thank you message");
        assertTrue(confirmationText.contains(String.valueOf(testOrderId)), "Confirmation should contain order ID");
        assertTrue(confirmationText.toLowerCase().contains("placed successfully"), "Confirmation should mention success");

        frame.dispose();
    }
}
