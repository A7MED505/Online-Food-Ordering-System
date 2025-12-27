package com.foodordering.dao;

import com.foodordering.models.Review;
import com.foodordering.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Review operations.
 * Handles CRUD operations for restaurant reviews.
 */
public class ReviewDAO {

    /**
     * Add a new review for a restaurant.
     * Note: Database has UNIQUE constraint (customer_id, restaurant_id).
     * 
     * @param review The review to add
     * @return true if successful, false otherwise
     */
    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (restaurant_id, customer_id, rating, comment) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, review.getRestaurantId());
            stmt.setInt(2, review.getCustomerId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        review.setReviewId(keys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding review: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get all reviews for a specific restaurant.
     * 
     * @param restaurantId The restaurant ID
     * @return List of reviews
     */
    public List<Review> getReviewsByRestaurant(int restaurantId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE restaurant_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restaurantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                        rs.getInt("review_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at")
                    );
                    reviews.add(review);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting reviews by restaurant: " + e.getMessage());
        }
        
        return reviews;
    }

    /**
     * Get a review by ID.
     * 
     * @param reviewId The review ID
     * @return The review or null if not found
     */
    public Review getReviewById(int reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reviewId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Review(
                        rs.getInt("review_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting review by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Calculate average rating for a restaurant from reviews table.
     * 
     * @param restaurantId The restaurant ID
     * @return Average rating (0.0 if no reviews)
     */
    public double calculateAverageRating(int restaurantId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE restaurant_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restaurantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating average rating: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Update an existing review.
     * 
     * @param review The review with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateReview(Review review) {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setInt(3, review.getReviewId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating review: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Delete a review.
     * 
     * @param reviewId The review ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting review: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get all reviews by a specific customer.
     * 
     * @param customerId The customer ID
     * @return List of reviews
     */
    public List<Review> getReviewsByCustomer(int customerId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE customer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                        rs.getInt("review_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at")
                    );
                    reviews.add(review);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting reviews by customer: " + e.getMessage());
        }
        
        return reviews;
    }
}
