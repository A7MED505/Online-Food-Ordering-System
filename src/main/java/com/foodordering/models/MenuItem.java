package com.foodordering.models;

/**
 * Represents a menu item in a restaurant.
 * Demonstrates encapsulation with private fields and validation.
 */
public class MenuItem {
    
    private int itemId;
    private int restaurantId;
    private String name;
    private double price;
    private String description;
    private boolean available;

    /**
     * Constructor for MenuItem.
     *
     * @param itemId Item ID
     * @param restaurantId Restaurant ID this item belongs to
     * @param name Name of the menu item
     * @param price Price of the item (must be >= 0)
     * @param description Description of the item
     * @param available Whether the item is available
     * @throws IllegalArgumentException if validation fails
     */
    public MenuItem(int itemId, int restaurantId, String name, double price, String description, boolean available) {
        validateName(name);
        validatePrice(price);
        
        this.itemId = itemId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    /**
     * Validates the menu item name.
     *
     * @param name Name to validate
     * @throws IllegalArgumentException if name is null or empty
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    /**
     * Validates the price.
     *
     * @param price Price to validate
     * @throws IllegalArgumentException if price is negative
     */
    private void validatePrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price must be greater than or equal to 0");
        }
    }

    // Getters
    public int getItemId() {
        return itemId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }

    // Setters
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setPrice(double price) {
        validatePrice(price);
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return String.format("MenuItem{id=%d, restaurant=%d, name='%s', price=$%.2f, available=%s}", 
                             itemId, restaurantId, name, price, available ? "Available" : "Unavailable");
    }
}
