package dao;

import database.DBConnection;
import model.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO payment (booking_id, amount, payment_date, payment_method, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, payment.getBookingId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentDate());
            ps.setString(4, payment.getPaymentMethod());
            ps.setString(5, payment.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error processing Payment: " + e.getMessage());
        }
        return false;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractPayment(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Payments: " + e.getMessage());
        }
        return list;
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPayment(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Payments by Booking ID: " + e.getMessage());
        }
        return list;
    }

    public Payment searchPaymentById(int paymentId) {
        String sql = "SELECT * FROM payment WHERE payment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPayment(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Payment by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE payment SET booking_id = ?, amount = ?, payment_date = ?, payment_method = ?, status = ? WHERE payment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, payment.getBookingId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentDate());
            ps.setString(4, payment.getPaymentMethod());
            ps.setString(5, payment.getStatus());
            ps.setInt(6, payment.getPaymentId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Payment: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePaymentStatusByBookingId(int bookingId, String status) {
        String sql = "UPDATE payment SET status = ? WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Payment status by Booking ID: " + e.getMessage());
        }
        return false;
    }

    public boolean deletePayment(int paymentId) {
        String sql = "DELETE FROM payment WHERE payment_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Payment: " + e.getMessage());
        }
        return false;
    }

    private Payment extractPayment(ResultSet rs) throws Exception {
        return new Payment(
            rs.getInt("payment_id"),
            rs.getInt("booking_id"),
            rs.getDouble("amount"),
            rs.getString("payment_date"),
            rs.getString("payment_method"),
            rs.getString("status")
        );
    }
}
