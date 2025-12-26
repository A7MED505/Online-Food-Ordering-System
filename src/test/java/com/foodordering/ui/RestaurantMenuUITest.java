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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests for Restaurant & Menu UI layers.
 */
class RestaurantMenuUITest {

    private RestaurantDAO restaurantDAO;
    private MenuItemDAO menuItemDAO;
    private Cart cart;
    private Restaurant restaurant;
    private MenuItem item;

    @BeforeEach
    void setup() throws SQLException {
        restaurantDAO = new RestaurantDAO();
        menuItemDAO = new MenuItemDAO();
        cart = new Cart();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM menu_items WHERE name LIKE 'UI Test %'");
            stmt.execute("DELETE FROM restaurants WHERE name LIKE 'UI Test %'");
        }

        restaurant = new Restaurant(0, "UI Test Restaurant", "123 UI St", "555-0000", 4.2);
        restaurantDAO.addRestaurant(restaurant);

        item = new MenuItem(0, restaurant.getRestaurantId(), "UI Test Pizza", 9.99, "Tasty", true);
        menuItemDAO.addMenuItem(item);
    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM menu_items WHERE name LIKE 'UI Test %'");
            stmt.execute("DELETE FROM restaurants WHERE name LIKE 'UI Test %'");
        }
    }

    @Test
    @DisplayName("Display restaurants list")
    void testDisplayRestaurants() {
        RestaurantFrame frame = new RestaurantFrame(cart, restaurantDAO, menuItemDAO);
        frame.loadRestaurants();

        DefaultListModel<String> model = frame.getRestaurantListModel();
        assertTrue(model.getSize() >= 1, "Should load at least one restaurant");
        boolean found = IntStream.range(0, model.getSize())
                .mapToObj(model::getElementAt)
                .anyMatch(name -> name.equals("UI Test Restaurant"));
        assertTrue(found, "UI Test Restaurant should be displayed");
    }

    @Test
    @DisplayName("Load menu items for restaurant")
    void testLoadMenu() {
        MenuFrame frame = new MenuFrame(restaurant, cart, menuItemDAO);
        frame.loadMenu();

        DefaultTableModel model = frame.getTableModel();
        assertEquals(1, model.getRowCount());
        assertEquals("UI Test Pizza", model.getValueAt(0, 0));
        assertEquals(9.99, (double) model.getValueAt(0, 1));
    }

    @Test
    @DisplayName("Add menu item to cart")
    void testAddToCart() {
        MenuFrame frame = new MenuFrame(restaurant, cart, menuItemDAO);
        frame.loadMenu();

        frame.addSelectedToCart(0, 2);

        List<?> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(19.98, cart.calculateTotal());
    }
}
