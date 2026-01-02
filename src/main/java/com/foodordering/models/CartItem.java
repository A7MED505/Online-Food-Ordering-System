package com.foodordering.models;

/**
 * Represents an item in shopping cart with quantity and pricing.
 */
public class CartItem {
    private final int itemId;
    private final String name;
    private final double unitPrice;
    private int quantity;

    /**
     * Creates cart item.
     * @param itemId Item ID (must be > 0)
     * @param name Item name (not empty)
     * @param unitPrice Price per unit (>= 0)
     * @param quantity Initial quantity (> 0)
     */
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

    /**
     * Updates item quantity.
     * @param quantity New quantity (must be > 0)
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
    }

    /** @return Total price for this item (unitPrice * quantity) */
    public double getSubtotal() {
        return Math.round(unitPrice * quantity * 100.0) / 100.0;
    }
}
