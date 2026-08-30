package dao;

import database.DBConnection;
import model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookingDAO {

    public int addBooking(Booking booking) {
        if (booking.getPnrCode() == null || booking.getPnrCode().trim().isEmpty()) {
            booking.setPnrCode(generatePNR());
        }

        String sql = "INSERT INTO booking (customer_id, flight_id, booking_date, status, total_amount, pnr_code, cabin_class) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getFlightId());
            ps.setString(3, booking.getBookingDate());
            ps.setString(4, booking.getStatus());
            ps.setDouble(5, booking.getTotalAmount());
            ps.setString(6, booking.getPnrCode());
            ps.setString(7, booking.getCabinClass());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating Booking: " + e.getMessage());
        }
        return -1;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM booking";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractBooking(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Bookings: " + e.getMessage());
        }
        return list;
    }

    public List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractBooking(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Customer Bookings: " + e.getMessage());
        }
        return list;
    }

    public Booking searchBookingById(int bookingId) {
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBooking(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Booking by ID: " + e.getMessage());
        }
        return null;
    }

    public Booking searchBookingByPNR(String pnrCode) {
        String sql = "SELECT * FROM booking WHERE pnr_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pnrCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBooking(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Booking by PNR: " + e.getMessage());
        }
        return null;
    }

    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE booking SET customer_id = ?, flight_id = ?, booking_date = ?, status = ?, total_amount = ?, pnr_code = ?, cabin_class = ? WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getFlightId());
            ps.setString(3, booking.getBookingDate());
            ps.setString(4, booking.getStatus());
            ps.setDouble(5, booking.getTotalAmount());
            ps.setString(6, booking.getPnrCode());
            ps.setString(7, booking.getCabinClass());
            ps.setInt(8, booking.getBookingId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Booking: " + e.getMessage());
        }
        return false;
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE booking SET status = ? WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Booking status: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM booking WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Booking: " + e.getMessage());
        }
        return false;
    }

    private Booking extractBooking(ResultSet rs) throws Exception {
        String pnr = getSafeString(rs, "pnr_code", "SKY7X9");
        String cabin = getSafeString(rs, "cabin_class", "Economy");
        return new Booking(
            rs.getInt("booking_id"),
            rs.getInt("customer_id"),
            rs.getInt("flight_id"),
            rs.getString("booking_date"),
            rs.getString("status"),
            rs.getDouble("total_amount"),
            pnr,
            cabin
        );
    }

    private String getSafeString(ResultSet rs, String col, String defaultVal) {
        try {
            String val = rs.getString(col);
            return val != null ? val : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private String generatePNR() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder("SKY");
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
