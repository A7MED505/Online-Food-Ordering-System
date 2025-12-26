package com.foodordering.models;

/**
 * Represents an item stored in the shopping cart.
 */
public class CartItem {
    private final int itemId;
    private final String name;
    private final double unitPrice;
    private int quantity;

    public CartItem(int itemId, String name, double unitPrice, int quantity) {
        if (itemId <= 0) throw new IllegalArgumentException("itemId must be positive");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be empty");
        if (unitPrice < 0) throw new IllegalArgumentException("unitPrice must be >= 0");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.itemId = itemId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return Math.round(unitPrice * quantity * 100.0) / 100.0;
    }
}
