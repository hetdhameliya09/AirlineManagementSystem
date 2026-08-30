package dao;

import database.DBConnection;
import model.Taxi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TaxiDAO {

    public boolean addTaxi(Taxi taxi) {
        String sql = "INSERT INTO taxi (driver_name, phone_number, vehicle_number, vehicle_type, price_per_km, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, taxi.getDriverName());
            ps.setString(2, taxi.getPhoneNumber());
            ps.setString(3, taxi.getVehicleNumber());
            ps.setString(4, taxi.getVehicleType());
            ps.setDouble(5, taxi.getPricePerKm());
            ps.setString(6, taxi.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Taxi: " + e.getMessage());
        }
        return false;
    }

    public List<Taxi> getAllTaxis() {
        List<Taxi> list = new ArrayList<>();
        String sql = "SELECT * FROM taxi";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractTaxi(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Taxis: " + e.getMessage());
        }
        return list;
    }

    public Taxi searchTaxiById(int taxiId) {
        String sql = "SELECT * FROM taxi WHERE taxi_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taxiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractTaxi(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Taxi by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Taxi> searchTaxisByQuery(String query) {
        List<Taxi> list = new ArrayList<>();
        String sql = "SELECT * FROM taxi WHERE driver_name LIKE ? OR vehicle_number LIKE ? OR vehicle_type LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractTaxi(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Taxis: " + e.getMessage());
        }
        return list;
    }

    public boolean updateTaxi(Taxi taxi) {
        String sql = "UPDATE taxi SET driver_name = ?, phone_number = ?, vehicle_number = ?, vehicle_type = ?, price_per_km = ?, status = ? WHERE taxi_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, taxi.getDriverName());
            ps.setString(2, taxi.getPhoneNumber());
            ps.setString(3, taxi.getVehicleNumber());
            ps.setString(4, taxi.getVehicleType());
            ps.setDouble(5, taxi.getPricePerKm());
            ps.setString(6, taxi.getStatus());
            ps.setInt(7, taxi.getTaxiId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Taxi: " + e.getMessage());
        }
        return false;
    }

    public boolean updateTaxiStatus(int taxiId, String status) {
        String sql = "UPDATE taxi SET status = ? WHERE taxi_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, taxiId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Taxi status: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteTaxi(int taxiId) {
        String sql = "DELETE FROM taxi WHERE taxi_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taxiId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Taxi: " + e.getMessage());
        }
        return false;
    }

    private Taxi extractTaxi(ResultSet rs) throws Exception {
        return new Taxi(
            rs.getInt("taxi_id"),
            rs.getString("driver_name"),
            rs.getString("phone_number"),
            rs.getString("vehicle_number"),
            rs.getString("vehicle_type"),
            rs.getDouble("price_per_km"),
            rs.getString("status")
        );
    }
}
