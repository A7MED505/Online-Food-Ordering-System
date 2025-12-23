package com.foodordering.models;

/**
 * Customer entity extends User with additional profile fields.
 */
public class Customer extends User {
    private String address;
    private String phone;

    public Customer(int id, String username, String email, String rawPassword, String address, String phone) {
        super(id, username, email, rawPassword);
        this.address = address;
        this.phone = phone;
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
}
