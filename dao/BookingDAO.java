package dao;

import database.DBConnection;
import model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookingDAO {

    public int addBooking(Booking booking) {
        String sql = "INSERT INTO booking (customer_id, flight_id, booking_date, status, total_amount) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getFlightId());
            ps.setString(3, booking.getBookingDate());
            ps.setString(4, booking.getStatus());
            ps.setDouble(5, booking.getTotalAmount());
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

    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE booking SET customer_id = ?, flight_id = ?, booking_date = ?, status = ?, total_amount = ? WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, booking.getCustomerId());
            ps.setInt(2, booking.getFlightId());
            ps.setString(3, booking.getBookingDate());
            ps.setString(4, booking.getStatus());
            ps.setDouble(5, booking.getTotalAmount());
            ps.setInt(6, booking.getBookingId());
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

    public boolean deleteBooking() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Booking ID: ");
        if (sc.hasNextInt()) {
            int id = sc.nextInt();
            return deleteBooking(id);
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

    public boolean bookFlightAtomic(int customerId, int flightId, String bookingDate, String passengerName, int passengerAge, String passengerGender, String seatNumber) {
        String procSql = "{call sp_book_flight(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = DBConnection.getConnection();
             java.sql.CallableStatement cs = con.prepareCall(procSql)) {
            cs.setInt(1, customerId);
            cs.setInt(2, flightId);
            cs.setDate(3, java.sql.Date.valueOf(bookingDate));
            cs.setString(4, passengerName);
            cs.setInt(5, passengerAge);
            cs.setString(6, passengerGender);
            cs.setString(7, seatNumber);
            cs.registerOutParameter(8, java.sql.Types.INTEGER);
            cs.registerOutParameter(9, java.sql.Types.INTEGER);
            cs.registerOutParameter(10, java.sql.Types.VARCHAR);
            cs.registerOutParameter(11, java.sql.Types.INTEGER);
            cs.execute();
            int resultCode = cs.getInt(11);
            return resultCode > 0 || cs.getInt(8) > 0;
        } catch (Exception e) {
            Booking b = new Booking(customerId, flightId, bookingDate, "CONFIRMED", 0.0);
            int bId = addBooking(b);
            return bId > 0;
        }
    }

    private Booking extractBooking(ResultSet rs) throws Exception {
        return new Booking(
            rs.getInt("booking_id"),
            rs.getInt("customer_id"),
            rs.getInt("flight_id"),
            rs.getString("booking_date"),
            rs.getString("status"),
            rs.getDouble("total_amount")
        );
    }
}
