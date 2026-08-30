package dao;

import database.DBConnection;
import model.Passenger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAO {

    public int addPassenger(Passenger passenger) {
        String sql = "INSERT INTO passenger (booking_id, name, age, gender, seat_number, nationality, meal_preference) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, passenger.getBookingId());
            ps.setString(2, passenger.getName());
            ps.setInt(3, passenger.getAge());
            ps.setString(4, passenger.getGender());
            ps.setString(5, passenger.getSeatNumber());
            ps.setString(6, passenger.getNationality());
            ps.setString(7, passenger.getMealPreference());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error adding Passenger: " + e.getMessage());
        }
        return -1;
    }

    public List<Passenger> getAllPassengers() {
        List<Passenger> list = new ArrayList<>();
        String sql = "SELECT * FROM passenger";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractPassenger(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Passengers: " + e.getMessage());
        }
        return list;
    }

    public List<Passenger> getPassengersByBookingId(int bookingId) {
        List<Passenger> list = new ArrayList<>();
        String sql = "SELECT * FROM passenger WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPassenger(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Passengers by Booking ID: " + e.getMessage());
        }
        return list;
    }

    public Passenger searchPassengerById(int passengerId) {
        String sql = "SELECT * FROM passenger WHERE passenger_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPassenger(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Passenger by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePassenger(Passenger passenger) {
        String sql = "UPDATE passenger SET booking_id = ?, name = ?, age = ?, gender = ?, seat_number = ?, nationality = ?, meal_preference = ? WHERE passenger_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, passenger.getBookingId());
            ps.setString(2, passenger.getName());
            ps.setInt(3, passenger.getAge());
            ps.setString(4, passenger.getGender());
            ps.setString(5, passenger.getSeatNumber());
            ps.setString(6, passenger.getNationality());
            ps.setString(7, passenger.getMealPreference());
            ps.setInt(8, passenger.getPassengerId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Passenger: " + e.getMessage());
        }
        return false;
    }

    public boolean deletePassenger(int passengerId) {
        String sql = "DELETE FROM passenger WHERE passenger_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Passenger: " + e.getMessage());
        }
        return false;
    }

    private Passenger extractPassenger(ResultSet rs) throws Exception {
        String nationality = getSafeString(rs, "nationality", "Indian");
        String meal = getSafeString(rs, "meal_preference", "Standard");
        return new Passenger(
            rs.getInt("passenger_id"),
            rs.getInt("booking_id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("gender"),
            rs.getString("seat_number"),
            nationality,
            meal
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
}
