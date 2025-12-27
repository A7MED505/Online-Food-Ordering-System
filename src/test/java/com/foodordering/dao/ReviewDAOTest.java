package com.foodordering.dao;

import com.foodordering.models.Customer;
import com.foodordering.models.Restaurant;
import com.foodordering.models.Review;
import com.foodordering.models.User;
import com.foodordering.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ReviewDAO.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewDAOTest {

    private static ReviewDAO reviewDAO;
    private static UserDAO userDAO;
    private static RestaurantDAO restaurantDAO;

    private static int testUserId;
    private static int testCustomerId;
    private static int testRestaurantId;

    @BeforeAll
    static void setup() throws SQLException {
        reviewDAO = new ReviewDAO();
        userDAO = new UserDAO();
        restaurantDAO = new RestaurantDAO();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        // Create test user and customer
        User testUser = new User(0, "review_test_user", "review@test.com", "TestPass123");
        userDAO.registerUser(testUser);
        testUserId = testUser.getId();

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO customers (user_id, address, phone) VALUES (" + testUserId + ", '111 Review St', '555-1111')");
            var rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
            if (rs.next()) {
                testCustomerId = rs.getInt(1);
            }
            rs.close();
        }

        // Create test restaurant
        Restaurant testRestaurant = new Restaurant(0, "Review Test Restaurant", "222 Test Ave", "555-2222", 0.0);
        restaurantDAO.addRestaurant(testRestaurant);
        testRestaurantId = testRestaurant.getRestaurantId();
    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM reviews WHERE customer_id = " + testCustomerId);
        }
    }

    @AfterAll
    static void teardown() throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM reviews WHERE customer_id = " + testCustomerId);
            stmt.execute("DELETE FROM restaurants WHERE restaurant_id = " + testRestaurantId);
            stmt.execute("DELETE FROM customers WHERE customer_id = " + testCustomerId);
            stmt.execute("DELETE FROM users WHERE user_id = " + testUserId);
        }
        DatabaseConnection.getInstance().closeConnection();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Test add review")
    void testAddReview() {
        Review review = new Review(0, testRestaurantId, testCustomerId, 5, "Excellent food!");
        
        boolean result = reviewDAO.addReview(review);
        
        assertTrue(result, "Review should be added successfully");
        assertTrue(review.getReviewId() > 0, "Review ID should be set after insertion");
        
        // Verify review was added
        Review retrieved = reviewDAO.getReviewById(review.getReviewId());
        assertNotNull(retrieved, "Review should be retrievable");
        assertEquals(5, retrieved.getRating());
        assertEquals("Excellent food!", retrieved.getComment());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Test calculate average rating")
    void testCalculateAverageRating() {
        // Simple test: Add one review and verify rating calculation
        Review review1 = new Review(0, testRestaurantId, testCustomerId, 5, "Perfect!");
        reviewDAO.addReview(review1);
        
        // Calculate average: should be 5.0
        double avgRating = reviewDAO.calculateAverageRating(testRestaurantId);
        
        assertEquals(5.0, avgRating, 0.01, "Average rating should be 5.0");
        
        // Delete and add another rating
        reviewDAO.deleteReview(review1.getReviewId());
        Review review2 = new Review(0, testRestaurantId, testCustomerId, 3, "Good");
        reviewDAO.addReview(review2);
        
        // Calculate average: should be 3.0
        avgRating = reviewDAO.calculateAverageRating(testRestaurantId);
        
        assertEquals(3.0, avgRating, 0.01, "Average rating should be 3.0");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Test get reviews by restaurant")
    void testGetReviewsByRestaurant() {
        // Add a review
        Review review = new Review(0, testRestaurantId, testCustomerId, 4, "Very good food");
        reviewDAO.addReview(review);
        
        // Get all reviews for the restaurant
        List<Review> reviews = reviewDAO.getReviewsByRestaurant(testRestaurantId);
        
        assertNotNull(reviews, "Reviews list should not be null");
        assertFalse(reviews.isEmpty(), "Should have at least one review");
        
        boolean found = false;
        for (Review r : reviews) {
            if (r.getReviewId() == review.getReviewId()) {
                found = true;
                assertEquals(testRestaurantId, r.getRestaurantId());
                assertEquals(testCustomerId, r.getCustomerId());
                assertEquals(4, r.getRating());
                assertEquals("Very good food", r.getComment());
                break;
            }
        }
        
        assertTrue(found, "Added review should be in the list");
    }
}
