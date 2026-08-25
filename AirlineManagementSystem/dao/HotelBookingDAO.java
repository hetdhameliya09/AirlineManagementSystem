package dao;

import database.DBConnection;
import model.HotelBooking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HotelBookingDAO {

    public boolean addHotelBooking(HotelBooking booking) {
        String sql = "INSERT INTO hotel_booking (customer_id, hotel_id, check_in_date, check_out_date, num_rooms, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getHotelId());
            ps.setString(3, booking.getCheckInDate());
            ps.setString(4, booking.getCheckOutDate());
            ps.setInt(5, booking.getNumRooms());
            ps.setDouble(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error creating Hotel Booking: " + e.getMessage());
        }
        return false;
    }

    public List<HotelBooking> getAllHotelBookings() {
        List<HotelBooking> list = new ArrayList<>();
        String sql = "SELECT * FROM hotel_booking";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractHotelBooking(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Hotel Bookings: " + e.getMessage());
        }
        return list;
    }

    public List<HotelBooking> getHotelBookingsByCustomerId(int customerId) {
        List<HotelBooking> list = new ArrayList<>();
        String sql = "SELECT * FROM hotel_booking WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractHotelBooking(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Hotel Bookings by Customer ID: " + e.getMessage());
        }
        return list;
    }

    public HotelBooking searchHotelBookingById(int hotelBookingId) {
        String sql = "SELECT * FROM hotel_booking WHERE hotel_booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelBookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractHotelBooking(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Hotel Booking by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateHotelBooking(HotelBooking booking) {
        String sql = "UPDATE hotel_booking SET customer_id = ?, hotel_id = ?, check_in_date = ?, check_out_date = ?, num_rooms = ?, total_price = ?, status = ? WHERE hotel_booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getHotelId());
            ps.setString(3, booking.getCheckInDate());
            ps.setString(4, booking.getCheckOutDate());
            ps.setInt(5, booking.getNumRooms());
            ps.setDouble(6, booking.getTotalPrice());
            ps.setString(7, booking.getStatus());
            ps.setInt(8, booking.getHotelBookingId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Hotel Booking: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteHotelBooking(int hotelBookingId) {
        String sql = "DELETE FROM hotel_booking WHERE hotel_booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelBookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Hotel Booking: " + e.getMessage());
        }
        return false;
    }

    private HotelBooking extractHotelBooking(ResultSet rs) throws Exception {
        return new HotelBooking(
            rs.getInt("hotel_booking_id"),
            rs.getInt("customer_id"),
            rs.getInt("hotel_id"),
            rs.getString("check_in_date"),
            rs.getString("check_out_date"),
            rs.getInt("num_rooms"),
            rs.getDouble("total_price"),
            rs.getString("status")
        );
    }
}
