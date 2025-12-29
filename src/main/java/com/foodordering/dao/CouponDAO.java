package com.foodordering.dao;

import com.foodordering.models.Coupon;
import com.foodordering.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CouponDAO {

    public Coupon getCouponByCode(String code) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT coupon_id, code, discount_percent, valid_from, valid_until, active FROM coupons WHERE code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoupon(rs);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error getting coupon by code: " + ex.getMessage());
        }
        return null;
    }

    
    public Coupon getCouponById(int couponId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT coupon_id, code, discount_percent, valid_from, valid_until, active FROM coupons WHERE coupon_id = ?")) {
            ps.setInt(1, couponId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoupon(rs);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error getting coupon by id: " + ex.getMessage());
        }
        return null;
    }

    public List<Coupon> getActiveCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT coupon_id, code, discount_percent, valid_from, valid_until, active FROM coupons WHERE active = TRUE")) {
            while (rs.next()) {
                coupons.add(mapResultSetToCoupon(rs));
            }
        } catch (SQLException ex) {
            System.err.println("Error getting active coupons: " + ex.getMessage());
        }
        return coupons;
    }

    public int addCoupon(Coupon coupon) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO coupons (code, discount_percent, valid_from, valid_until, active) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, coupon.getCode());
            ps.setInt(2, (int) coupon.getValue());
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setDate(4, Date.valueOf(coupon.getExpirationDate()));
            ps.setBoolean(5, coupon.isActive());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int couponId = keys.getInt(1);
                        return couponId;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error adding coupon: " + ex.getMessage());
        }
        return 0;
    }

    public boolean updateCoupon(Coupon coupon) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE coupons SET valid_until = ?, active = ? WHERE coupon_id = ?")) {
            ps.setDate(1, Date.valueOf(coupon.getExpirationDate()));
            ps.setBoolean(2, coupon.isActive());
            ps.setInt(3, coupon.getCouponId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error updating coupon: " + ex.getMessage());
        }
        return false;
    }

    public boolean deleteCoupon(int couponId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM coupons WHERE coupon_id = ?")) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error deleting coupon: " + ex.getMessage());
        }
        return false;
    }

    public boolean isValidCoupon(String code) {
        Coupon coupon = getCouponByCode(code);
        return coupon != null && coupon.isActive();
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        int couponId = rs.getInt("coupon_id");
        String code = rs.getString("code");
        int discountPercent = rs.getInt("discount_percent");
        LocalDate validUntil = rs.getDate("valid_until").toLocalDate();
        boolean active = rs.getBoolean("active");

        return new Coupon(couponId, code, Coupon.Type.PERCENTAGE, discountPercent, validUntil, active);
    }
}
