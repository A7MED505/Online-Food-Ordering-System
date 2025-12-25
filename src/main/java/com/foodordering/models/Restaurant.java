package com.foodordering.models;

import java.util.List;

/**
 * Restaurant model representing a restaurant entity.
 */
public class Restaurant {
    private int restaurantId;
    private String name;
    private String address;
    private String phone;
    private double rating; // 0.0 to 5.0

    public Restaurant(int restaurantId, String name, String address, String phone, double rating) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.restaurantId = restaurantId;
        this.name = name.trim();
        this.address = address;
        this.phone = phone;
        this.rating = rating;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
    }

    /**
     * Calculates average rating from a list of integer ratings (1..5).
     * Returns 0.0 if list is null or empty.
     */
    public static double calculateAverageRating(List<Integer> ratings) {
        if (ratings == null || ratings.isEmpty()) return 0.0;
        int sum = 0;
        int count = 0;
        for (Integer r : ratings) {
            if (r == null) continue;
            if (r < 1 || r > 5) {
                throw new IllegalArgumentException("Each rating must be between 1 and 5");
            }
            sum += r;
            count++;
        }
        if (count == 0) return 0.0;
        return Math.round(((double) sum / count) * 100.0) / 100.0; // round to 2 decimals
    }

    @Override
    public String toString() {
        return String.format("Restaurant{id=%d, name='%s', rating=%.2f}", restaurantId, name, rating);
    }
}
