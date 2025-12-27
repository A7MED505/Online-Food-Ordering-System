package com.foodordering.dao;

import com.foodordering.models.Order;
import com.foodordering.models.OrderItem;
import com.foodordering.models.User;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderDAOTest {

    private static OrderDAO orderDAO;
    private static UserDAO userDAO;
    private static RestaurantDAO restaurantDAO;
    private static MenuItemDAO menuItemDAO;
    private static int testCustomerId;
    private static int testRestaurantId;
    private static int testMenuItem1Id;
    private static int testMenuItem2Id;

    @BeforeAll
    static void setup() throws SQLException {
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        restaurantDAO = new RestaurantDAO();
        menuItemDAO = new MenuItemDAO();
        
        Connection conn = DatabaseConnection.getInstance().getConnection();
        
        // Create test user and customer
        User testUser = new User(0, "order_test_user", "order@test.com", "TestPass123");
        userDAO.registerUser(testUser);
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO customers (user_id, address, phone) VALUES (" + testUser.getId() + ", '123 Test St', '555-1234')");
            var rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) {
                testCustomerId = rs.getInt(1);
            }
            rs.close();
        }
        
        // Create test restaurant
        com.foodordering.models.Restaurant testRestaurant = new com.foodordering.models.Restaurant(0, "Order Test Restaurant", "456 Test Ave", "555-5678", 4.5);
        restaurantDAO.addRestaurant(testRestaurant);
        testRestaurantId = testRestaurant.getRestaurantId();
        
        // Create test menu items
        com.foodordering.models.MenuItem item1 = new com.foodordering.models.MenuItem(0, testRestaurantId, "Test Pizza", 15.99, "Test pizza desc", true);
        com.foodordering.models.MenuItem item2 = new com.foodordering.models.MenuItem(0, testRestaurantId, "Test Burger", 12.50, "Test burger desc", true);
        menuItemDAO.addMenuItem(item1);
        menuItemDAO.addMenuItem(item2);
        testMenuItem1Id = item1.getItemId();
        testMenuItem2Id = item2.getItemId();
    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM order_items WHERE order_id IN (SELECT order_id FROM orders WHERE customer_id = " + testCustomerId + ")");
            stmt.execute("DELETE FROM orders WHERE customer_id = " + testCustomerId);
        }
    }

    @AfterAll
    static void teardown() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM order_items WHERE order_id IN (SELECT order_id FROM orders WHERE customer_id = " + testCustomerId + ")");
            stmt.execute("DELETE FROM orders WHERE customer_id = " + testCustomerId);
            stmt.execute("DELETE FROM menu_items WHERE restaurant_id = " + testRestaurantId);
            stmt.execute("DELETE FROM restaurants WHERE restaurant_id = " + testRestaurantId);
            stmt.execute("DELETE FROM customers WHERE customer_id = " + testCustomerId);
            stmt.execute("DELETE FROM users WHERE username = 'order_test_user'");
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Test create order with items")
    void testCreateOrder() {
        Order order = new Order(0, testCustomerId, testRestaurantId, 0.0, "pending", null);
        
        OrderItem item1 = new OrderItem(0, 0, testMenuItem1Id, 2, 15.99);
        OrderItem item2 = new OrderItem(0, 0, testMenuItem2Id, 1, 12.50);
        order.addItem(item1);
        order.addItem(item2);
        
        double calculatedTotal = order.calculateTotal();
        order.setTotalPrice(calculatedTotal);
        
        boolean result = orderDAO.createOrder(order);
        
        assertTrue(result, "Order should be created successfully");
        assertTrue(order.getOrderId() > 0, "Order ID should be set after creation");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Test get order by ID")
    void testGetOrderById() {
        Order order = new Order(0, testCustomerId, testRestaurantId, 28.48, "pending", null);
        OrderItem item = new OrderItem(0, 0, testMenuItem1Id, 1, 15.99);
        order.addItem(item);
        orderDAO.createOrder(order);
        
        Order retrieved = orderDAO.getOrderById(order.getOrderId());
        
        assertNotNull(retrieved, "Order should be retrieved");
        assertEquals(testCustomerId, retrieved.getCustomerId());
        assertEquals(testRestaurantId, retrieved.getRestaurantId());
        assertEquals("pending", retrieved.getStatus());
        assertFalse(retrieved.getItems().isEmpty(), "Order should have items");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Test get orders by customer ID")
    void testGetOrdersByCustomerId() {
        Order order1 = new Order(0, testCustomerId, testRestaurantId, 15.99, "pending", null);
        Order order2 = new Order(0, testCustomerId, testRestaurantId, 12.50, "confirmed", null);
        
        orderDAO.createOrder(order1);
        orderDAO.createOrder(order2);
        
        List<Order> orders = orderDAO.getOrdersByCustomerId(testCustomerId);
        
        assertNotNull(orders);
        assertTrue(orders.size() >= 2, "Should have at least 2 orders");
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Test update order status")
    void testUpdateOrderStatus() {
        Order order = new Order(0, testCustomerId, testRestaurantId, 15.99, "pending", null);
        orderDAO.createOrder(order);
        
        boolean updated = orderDAO.updateOrderStatus(order.getOrderId(), "confirmed");
        assertTrue(updated, "Status should be updated");
        
        Order retrieved = orderDAO.getOrderById(order.getOrderId());
        assertEquals("confirmed", retrieved.getStatus());
    }
}
