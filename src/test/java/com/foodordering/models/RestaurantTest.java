package com.foodordering.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    @DisplayName("Test restaurant creation")
    void testRestaurantCreation() {
        Restaurant r = new Restaurant(0, "Test R", "Address", "555-0000", 4.2);
        assertEquals("Test R", r.getName());
        assertEquals("Address", r.getAddress());
        assertEquals("555-0000", r.getPhone());
        assertEquals(4.2, r.getRating(), 0.001);
    }

    @Test
    @DisplayName("Test invalid empty name throws")
    void testInvalidName() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Restaurant(0, " ", "Address", "555-0000", 4.0));
        assertEquals("Name cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("Test invalid rating range throws")
    void testInvalidRating() {
        Exception ex1 = assertThrows(IllegalArgumentException.class, () ->
                new Restaurant(0, "R", "Address", "555", -1.0));
        assertEquals("Rating must be between 0 and 5", ex1.getMessage());
        Exception ex2 = assertThrows(IllegalArgumentException.class, () ->
                new Restaurant(0, "R", "Address", "555", 5.5));
        assertEquals("Rating must be between 0 and 5", ex2.getMessage());
    }

    @Test
    @DisplayName("Test rating calculation average")
    void testRatingCalculation() {
        List<Integer> ratings = Arrays.asList(5, 4, 3, 5);
        double avg = Restaurant.calculateAverageRating(ratings);
        assertEquals(4.25, avg, 0.0001);
    }
}
