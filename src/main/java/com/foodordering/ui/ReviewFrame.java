package com.foodordering.ui;

import com.foodordering.dao.ReviewDAO;
import com.foodordering.models.Restaurant;
import com.foodordering.models.Review;
import com.foodordering.services.Session;
import com.foodordering.utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * UI frame to display and add restaurant reviews.
 */
public class ReviewFrame extends JFrame {
    private final Restaurant restaurant;
    private final ReviewDAO reviewDAO;
    private Integer customerId; // resolved on demand

    private final JTextArea reviewsArea = new JTextArea(15, 40);
    private final JComboBox<Integer> ratingCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
    private final JTextField commentField = new JTextField(30);

    public ReviewFrame(Restaurant restaurant, ReviewDAO reviewDAO) {
        this.restaurant = restaurant;
        this.reviewDAO = reviewDAO;
        initializeUI();
        loadReviews();
    }

    private void initializeUI() {
        setTitle("Reviews - " + restaurant.getName());
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top: Restaurant info and current rating
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel nameLabel = new JLabel(restaurant.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel ratingLabel = new JLabel(String.format("⭐ Rating: %.2f / 5.00", restaurant.getRating()), SwingConstants.CENTER);
        ratingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(nameLabel);
        topPanel.add(ratingLabel);

        // Center: Reviews list
        reviewsArea.setEditable(false);
        reviewsArea.setLineWrap(true);
        reviewsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(reviewsArea);

        // Bottom: Add review form
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Add Your Review"));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Rating:"));
        ratingCombo.setSelectedItem(5);
        formPanel.add(ratingCombo);
        formPanel.add(new JLabel("Comment:"));
        formPanel.add(commentField);

        JButton submitButton = new JButton("Submit Review");
        submitButton.addActionListener(e -> submitReview());

        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(submitButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadReviews() {
        reviewsArea.setText("");
        List<Review> reviews = reviewDAO.getReviewsByRestaurant(restaurant.getRestaurantId());
        
        if (reviews.isEmpty()) {
            reviewsArea.setText("No reviews yet. Be the first to review!");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Review r : reviews) {
                sb.append(String.format("⭐ %d/5 - %s\n", r.getRating(), r.getComment() != null ? r.getComment() : "(No comment)"));
                if (r.getCreatedAt() != null) {
                    sb.append("   " + r.getCreatedAt().toString() + "\n");
                }
                sb.append("\n");
            }
            reviewsArea.setText(sb.toString());
        }
    }

    private void submitReview() {
        if (!Session.getInstance().isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please login to submit a review.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int custId = resolveOrCreateCustomerId();
        if (custId <= 0) {
            JOptionPane.showMessageDialog(this, "Unable to resolve customer account.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer rating = (Integer) ratingCombo.getSelectedItem();
        String comment = commentField.getText().trim();

        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a comment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Review review = new Review(0, restaurant.getRestaurantId(), custId, rating, comment);
        boolean success = reviewDAO.addReview(review);

        if (success) {
            JOptionPane.showMessageDialog(this, "Review submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            commentField.setText("");
            ratingCombo.setSelectedItem(5);
            loadReviews();
            
            // Refresh restaurant rating from database
            double newRating = reviewDAO.calculateAverageRating(restaurant.getRestaurantId());
            restaurant.setRating(newRating);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit review. You may have already reviewed this restaurant.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int resolveOrCreateCustomerId() {
        if (customerId != null && customerId > 0) {
            return customerId;
        }

        int userId = Session.getInstance().getCurrentUser().getId();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Try resolve existing customer
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT customer_id FROM customers WHERE user_id = " + userId);
                if (rs.next()) {
                    customerId = rs.getInt(1);
                    return customerId;
                }
                rs.close();
            }
            // Create minimal customer record
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("INSERT INTO customers (user_id, address, phone) VALUES (" + userId + ", 'Unknown', '000-0000')");
                ResultSet keys = stmt.executeQuery("SELECT LAST_INSERT_ID()");
                if (keys.next()) {
                    customerId = keys.getInt(1);
                    return customerId;
                }
                keys.close();
            }
        } catch (Exception ex) {
            System.err.println("Error resolving customer: " + ex.getMessage());
        }
        
        return 0;
    }

    // Testing helper
    public String getReviewsText() {
        return reviewsArea.getText();
    }
}
