package com.foodordering.models;

import java.sql.Timestamp;

/**
 * Review model representing a customer review for a restaurant.
 */
public class Review {
    private int reviewId;
    private int restaurantId;
    private int customerId;
    private int rating; // 1-5
    private String comment;
    private Timestamp createdAt;

    public Review(int reviewId, int restaurantId, int customerId, int rating, String comment, Timestamp createdAt) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.reviewId = reviewId;
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Constructor without timestamp (for creating new reviews)
    public Review(int reviewId, int restaurantId, int customerId, int rating, String comment) {
        this(reviewId, restaurantId, customerId, rating, comment, null);
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Review{id=%d, restaurant=%d, customer=%d, rating=%d, comment='%s'}",
                reviewId, restaurantId, customerId, rating, comment != null ? comment.substring(0, Math.min(20, comment.length())) : "");
    }
}
