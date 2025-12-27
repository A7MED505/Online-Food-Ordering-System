package com.foodordering.ui;

import com.foodordering.dao.OrderDAO;
import com.foodordering.dao.RestaurantDAO;
import com.foodordering.dao.MenuItemDAO;
import com.foodordering.interfaces.Orderable;
import com.foodordering.models.Cart;
import com.foodordering.models.MenuItem;
import com.foodordering.models.Restaurant;
import com.foodordering.models.User;
import com.foodordering.payments.CashPayment;
import com.foodordering.services.PaymentService;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckoutUITest {

    private static Connection conn;
    private static RestaurantDAO restaurantDAO;
    private static MenuItemDAO menuItemDAO;
    private static OrderDAO orderDAO;

    private static Restaurant restaurant;
    private static MenuItem item1;
    private static User testUser;

    @BeforeAll
    static void setupAll() throws SQLException {
        conn = DatabaseConnection.getInstance().getConnection();
        restaurantDAO = new RestaurantDAO();
        menuItemDAO = new MenuItemDAO();
        orderDAO = new OrderDAO();

        // Clean remnants
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM orders WHERE customer_id IN (SELECT customer_id FROM customers WHERE user_id IN (SELECT user_id FROM users WHERE username LIKE 'checkout_test_user'))");
            stmt.execute("DELETE FROM customers WHERE user_id IN (SELECT user_id FROM users WHERE username LIKE 'checkout_test_user')");
            stmt.execute("DELETE FROM users WHERE username LIKE 'checkout_test_user'");
            stmt.execute("DELETE FROM menu_items WHERE name LIKE 'Checkout Test %'");
            stmt.execute("DELETE FROM restaurants WHERE name LIKE 'Checkout Test %'");
        }

        // Create base data
        testUser = new User(0, "checkout_test_user", "checkout@test.com", "TestPass123");
        // Use DAO to register user
        new com.foodordering.dao.UserDAO().registerUser(testUser);

        restaurant = new Restaurant(0, "Checkout Test Restaurant", "123 Test St", "555-0011", 4.1);
        restaurantDAO.addRestaurant(restaurant);

        item1 = new MenuItem(0, restaurant.getRestaurantId(), "Checkout Test Pizza", 11.50, "Tasty", true);
        menuItemDAO.addMenuItem(item1);
    }

    @AfterAll
    static void teardownAll() throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             Statement stmt = c.createStatement()) {
            stmt.execute("DELETE FROM orders WHERE customer_id IN (SELECT customer_id FROM customers WHERE user_id IN (SELECT user_id FROM users WHERE username LIKE 'checkout_test_user'))");
            stmt.execute("DELETE FROM customers WHERE user_id IN (SELECT user_id FROM users WHERE username LIKE 'checkout_test_user')");
            stmt.execute("DELETE FROM users WHERE username LIKE 'checkout_test_user'");
            stmt.execute("DELETE FROM menu_items WHERE name LIKE 'Checkout Test %'");
            stmt.execute("DELETE FROM restaurants WHERE name LIKE 'Checkout Test %'");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Payment processing via PaymentService")
    void testPaymentProcessing() {
        PaymentService ps = new PaymentService();
        Orderable method = new CashPayment("Cashier");
        assertTrue(ps.process(method, 20.0));
        assertFalse(ps.process(method, 0));
    }

    @Test
    @Order(2)
    @DisplayName("Successful checkout creates order and clears cart")
    void testSuccessfulCheckout() {
        Cart cart = new Cart();
        cart.addItem(item1, 2); // 23.00

        CheckoutFrame cf = new CheckoutFrame(testUser, cart, orderDAO);
        int orderId = cf.placeOrderForTest(new CashPayment("Cashier"));

        assertTrue(orderId > 0, "Order id should be set");
        assertEquals(0.0, cart.calculateTotal(), "Cart should be cleared after order");
    }

    @Test
    @Order(3)
    @DisplayName("Order is persisted and retrievable")
    void testOrderCreation() {
        Cart cart = new Cart();
        cart.addItem(item1, 1); // 11.50

        CheckoutFrame cf = new CheckoutFrame(testUser, cart, orderDAO);
        int orderId = cf.placeOrderForTest(new CashPayment("Cashier"));
        assertTrue(orderId > 0);

        var retrieved = orderDAO.getOrderById(orderId);
        assertNotNull(retrieved);
        assertEquals("pending", retrieved.getStatus());
        assertFalse(retrieved.getItems().isEmpty());
    }
}
