package com.foodordering.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Order model representing a customer order.
 */
public class Order {
    private int orderId;
    private int customerId;
    private int restaurantId;
    private double totalPrice;
    private String status; // pending, confirmed, preparing, shipped, delivered, cancelled
    private Integer couponId;
    private List<OrderItem> items;

    public Order(int orderId, int customerId, int restaurantId, double totalPrice, String status, Integer couponId) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("Total price cannot be negative");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.totalPrice = totalPrice;
        this.status = status.trim();
        this.couponId = couponId;
        this.items = new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("Total price cannot be negative");
        }
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        this.status = status.trim();
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    /**
     * Calculates total price from order items.
     */
    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getUnitPrice() * item.getQuantity();
        }
        return Math.round(total * 100.0) / 100.0; // round to 2 decimals
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, customer=%d, restaurant=%d, total=%.2f, status='%s'}",
                orderId, customerId, restaurantId, totalPrice, status);
    }
}
