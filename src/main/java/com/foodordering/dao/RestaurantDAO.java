package com.foodordering.dao;

import com.foodordering.models.Restaurant;
import com.foodordering.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Restaurant entity.
 */
public class RestaurantDAO {

    /**
     * Adds a new restaurant to the database.
     */
    public boolean addRestaurant(Restaurant restaurant) {
        String sql = "INSERT INTO restaurants (name, address, phone, rating) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getAddress());
            stmt.setString(3, restaurant.getPhone());
            stmt.setDouble(4, restaurant.getRating());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        restaurant.setRestaurantId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding restaurant: " + e.getMessage());
        }
        return false;
    }

    /**
     * Returns a restaurant by id or null if not found.
     */
    public Restaurant getRestaurantById(int id) {
        String sql = "SELECT * FROM restaurants WHERE restaurant_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting restaurant by id: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all restaurants ordered by name.
     */
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT * FROM restaurants ORDER BY name";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all restaurants: " + e.getMessage());
        }
        return list;
    }

    /**
     * Deletes a restaurant (for tests cleanup).
     */
    public boolean deleteRestaurant(int id) {
        String sql = "DELETE FROM restaurants WHERE restaurant_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting restaurant: " + e.getMessage());
        }
        return false;
    }

    private Restaurant mapRow(ResultSet rs) throws SQLException {
        return new Restaurant(
                rs.getInt("restaurant_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getDouble("rating")
        );
    }
}
