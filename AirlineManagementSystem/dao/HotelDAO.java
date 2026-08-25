package dao;

import database.DBConnection;
import model.Hotel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {

    public boolean addHotel(Hotel hotel) {
        String sql = "INSERT INTO hotel (hotel_name, city, price_per_night, available_rooms, rating) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hotel.getHotelName());
            ps.setString(2, hotel.getCity());
            ps.setDouble(3, hotel.getPricePerNight());
            ps.setInt(4, hotel.getAvailableRooms());
            ps.setDouble(5, hotel.getRating());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Hotel: " + e.getMessage());
        }
        return false;
    }

    public List<Hotel> getAllHotels() {
        List<Hotel> list = new ArrayList<>();
        String sql = "SELECT * FROM hotel";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractHotel(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Hotels: " + e.getMessage());
        }
        return list;
    }

    public Hotel searchHotelById(int hotelId) {
        String sql = "SELECT * FROM hotel WHERE hotel_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractHotel(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Hotel by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Hotel> searchHotelsByCity(String city) {
        List<Hotel> list = new ArrayList<>();
        String sql = "SELECT * FROM hotel WHERE city LIKE ? OR hotel_name LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String wildcard = "%" + city + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractHotel(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Hotels by city: " + e.getMessage());
        }
        return list;
    }

    public boolean updateHotel(Hotel hotel) {
        String sql = "UPDATE hotel SET hotel_name = ?, city = ?, price_per_night = ?, available_rooms = ?, rating = ? WHERE hotel_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hotel.getHotelName());
            ps.setString(2, hotel.getCity());
            ps.setDouble(3, hotel.getPricePerNight());
            ps.setInt(4, hotel.getAvailableRooms());
            ps.setDouble(5, hotel.getRating());
            ps.setInt(6, hotel.getHotelId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Hotel: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteHotel(int hotelId) {
        String sql = "DELETE FROM hotel WHERE hotel_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Hotel: " + e.getMessage());
        }
        return false;
    }

    private Hotel extractHotel(ResultSet rs) throws Exception {
        return new Hotel(
            rs.getInt("hotel_id"),
            rs.getString("hotel_name"),
            rs.getString("city"),
            rs.getDouble("price_per_night"),
            rs.getInt("available_rooms"),
            rs.getDouble("rating")
        );
    }
}
