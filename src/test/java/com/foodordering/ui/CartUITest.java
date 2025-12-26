package com.foodordering.ui;

import com.foodordering.dao.MenuItemDAO;
import com.foodordering.dao.RestaurantDAO;
import com.foodordering.models.Cart;
import com.foodordering.models.MenuItem;
import com.foodordering.models.Restaurant;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CartFrame UI.
 */
class CartUITest {

    private CartFrame cartFrame;
    private Cart cart;
    private RestaurantDAO restaurantDAO;
    private MenuItemDAO menuItemDAO;
    private Restaurant restaurant;
    private MenuItem item1, item2;

    @BeforeEach
    void setup() throws SQLException {
        cart = new Cart();
        restaurantDAO = new RestaurantDAO();
        menuItemDAO = new MenuItemDAO();
        cartFrame = new CartFrame(cart);

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM menu_items WHERE name LIKE 'CartUI Test %'");
            stmt.execute("DELETE FROM restaurants WHERE name LIKE 'CartUI Test %'");
        }

        // Create test restaurant and menu items
        restaurant = new Restaurant(0, "CartUI Test Restaurant", "123 Cart St", "555-0000", 4.0);
        restaurantDAO.addRestaurant(restaurant);

        item1 = new MenuItem(0, restaurant.getRestaurantId(), "CartUI Test Burger", 8.99, "Tasty", true);
        item2 = new MenuItem(0, restaurant.getRestaurantId(), "CartUI Test Fries", 3.49, "Crispy", true);
        menuItemDAO.addMenuItem(item1);
        menuItemDAO.addMenuItem(item2);
    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM menu_items WHERE name LIKE 'CartUI Test %'");
            stmt.execute("DELETE FROM restaurants WHERE name LIKE 'CartUI Test %'");
        }
    }

    @Test
    @DisplayName("Display cart items in table")
    void testDisplayCartItems() {
        cart.addItem(item1, 2);
        cart.addItem(item2, 1);
        cartFrame.loadCartItems();

        DefaultTableModel model = cartFrame.getTableModel();
        assertEquals(2, model.getRowCount(), "Should display 2 items");
        assertEquals("CartUI Test Burger", model.getValueAt(0, 0));
        assertEquals("CartUI Test Fries", model.getValueAt(1, 0));
    }

    @Test
    @DisplayName("Remove item from cart via UI")
    void testRemoveFromCart() {
        cart.addItem(item1, 2);
        cart.addItem(item2, 1);
        cartFrame.loadCartItems();

        assertEquals(2, cart.getItems().size());
        
        cart.removeItem(item1.getItemId());
        cartFrame.loadCartItems();

        assertEquals(1, cart.getItems().size());
        DefaultTableModel model = cartFrame.getTableModel();
        assertEquals(1, model.getRowCount());
    }

    @Test
    @DisplayName("Update item quantity in cart")
    void testUpdateQuantity() {
        cart.addItem(item1, 1);
        cartFrame.loadCartItems();

        assertEquals(8.99, cart.calculateTotal());

        cart.updateQuantity(item1.getItemId(), 3);
        cartFrame.loadCartItems();

        assertEquals(26.97, cart.calculateTotal());
        DefaultTableModel model = cartFrame.getTableModel();
        assertEquals(3, model.getValueAt(0, 2));
    }

    @Test
    @DisplayName("Clear entire cart")
    void testClearCart() {
        cart.addItem(item1, 2);
        cart.addItem(item2, 3);
        cartFrame.loadCartItems();

        assertEquals(2, cart.getItems().size());

        cart.clearCart();
        cartFrame.loadCartItems();

        assertEquals(0, cart.getItems().size());
        assertEquals(0.0, cart.calculateTotal());
        DefaultTableModel model = cartFrame.getTableModel();
        assertEquals(0, model.getRowCount());
    }

    @Test
    @DisplayName("Display correct total in label")
    void testTotalLabel() {
        cart.addItem(item1, 2); // 8.99 * 2 = 17.98
        cart.addItem(item2, 1); // 3.49 * 1 = 3.49
        // Total: 21.47
        cartFrame.loadCartItems();

        String totalText = cartFrame.getTotalLabelText();
        assertTrue(totalText.contains("21.47"), "Total should be $21.47");
    }
}
