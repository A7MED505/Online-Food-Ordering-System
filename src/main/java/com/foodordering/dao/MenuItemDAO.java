package com.foodordering.dao;

import com.foodordering.models.MenuItem;
import com.foodordering.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for MenuItem entity.
 * Handles all database operations for menu items.
 */
public class MenuItemDAO {

    /**
     * Adds a new menu item to the database.
     *
     * @param menuItem MenuItem object to add
     * @return true if successful, false otherwise
     */
    public boolean addMenuItem(MenuItem menuItem) {
        String sql = "INSERT INTO menu_items (restaurant_id, name, price, description, available) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, menuItem.getRestaurantId());
            stmt.setString(2, menuItem.getName());
            stmt.setDouble(3, menuItem.getPrice());
            stmt.setString(4, menuItem.getDescription());
            stmt.setBoolean(5, menuItem.isAvailable());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        menuItem.setItemId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Retrieves a menu item by its ID.
     *
     * @param itemId ID of the menu item
     * @return MenuItem object if found, null otherwise
     */
    public MenuItem getMenuItemById(int itemId) {
        String sql = "SELECT * FROM menu_items WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMenuItemFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting menu item by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Retrieves all menu items for a specific restaurant.
     *
     * @param restaurantId ID of the restaurant
     * @return List of MenuItem objects
     */
    public List<MenuItem> getMenuItemsByRestaurant(int restaurantId) {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE restaurant_id = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restaurantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(extractMenuItemFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting menu items by restaurant: " + e.getMessage());
        }
        
        return items;
    }

    /**
     * Retrieves all available menu items for a specific restaurant.
     *
     * @param restaurantId ID of the restaurant
     * @return List of available MenuItem objects
     */
    public List<MenuItem> getAvailableMenuItemsByRestaurant(int restaurantId) {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE restaurant_id = ? AND available = TRUE ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restaurantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(extractMenuItemFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting available menu items: " + e.getMessage());
        }
        
        return items;
    }

    /**
     * Updates an existing menu item.
     *
     * @param menuItem MenuItem object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateMenuItem(MenuItem menuItem) {
        String sql = "UPDATE menu_items SET name = ?, price = ?, description = ?, available = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, menuItem.getName());
            stmt.setDouble(2, menuItem.getPrice());
            stmt.setString(3, menuItem.getDescription());
            stmt.setBoolean(4, menuItem.isAvailable());
            stmt.setInt(5, menuItem.getItemId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Deletes a menu item from the database.
     *
     * @param itemId ID of the menu item to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteMenuItem(int itemId) {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting menu item: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Helper method to extract MenuItem from ResultSet.
     *
     * @param rs ResultSet positioned at a row
     * @return MenuItem object
     * @throws SQLException if database error occurs
     */
    private MenuItem extractMenuItemFromResultSet(ResultSet rs) throws SQLException {
        return new MenuItem(
            rs.getInt("item_id"),
            rs.getInt("restaurant_id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getString("description"),
            rs.getBoolean("available")
        );
    }
}
