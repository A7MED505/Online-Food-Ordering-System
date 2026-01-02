package com.foodordering.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Shopping cart to manage items prior to creating an order.
 * Supports quantity updates, coupon application, and total calculation.
 */
public class Cart {
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();
    private Coupon appliedCoupon;

    /** @return List of all items in cart */
    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    /** Removes all items and coupon from cart */
    public void clearCart() {
        items.clear();
        appliedCoupon = null;
    }

    /**
     * Adds item to cart. If item exists, increases quantity.
     * @param item MenuItem to add
     * @param quantity Quantity to add (must be > 0)
     */
    public void addItem(MenuItem item, int quantity) {
        if (item == null) throw new IllegalArgumentException("item must not be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        int id = item.getItemId();
        if (id <= 0) throw new IllegalArgumentException("MenuItem must have a valid id");
        if (item.getPrice() < 0) throw new IllegalArgumentException("MenuItem price must be >= 0");

        CartItem existing = items.get(id);
        if (existing == null) {
            items.put(id, new CartItem(id, item.getName(), item.getPrice(), quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
    }

    /**
     * Removes item from cart.
     * @param itemId ID of item to remove
     * @return true if item was removed
     */
    public boolean removeItem(int itemId) {
        return items.remove(itemId) != null;
    }

    /**
     * Updates quantity of existing item.
     * @param itemId Item ID
     * @param newQuantity New quantity (must be > 0)
     */
    public void updateQuantity(int itemId, int newQuantity) {
        if (newQuantity <= 0) throw new IllegalArgumentException("newQuantity must be > 0");
        CartItem existing = items.get(itemId);
        if (existing == null) throw new IllegalArgumentException("Item not in cart");
        existing.setQuantity(newQuantity);
    }

    /** Applies discount coupon to cart */
    public void applyCoupon(Coupon coupon) {
        this.appliedCoupon = coupon;
    }

    /** @return Currently applied coupon or null */
    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }

    /**
     * Calculates total price with coupon discount applied.
     * @return Total price (rounded to 2 decimals)
     */
    public double calculateTotal() {
        double subtotal = items.values().stream()
                .mapToDouble(CartItem::getSubtotal).sum();
        double discount = appliedCoupon == null ? 0.0 : appliedCoupon.computeDiscount(subtotal);
        double total = Math.max(0.0, subtotal - discount);
        return round2(total);
    }

    private double round2(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
