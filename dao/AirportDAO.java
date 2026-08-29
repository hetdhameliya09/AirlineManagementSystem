package dao;

import database.DBConnection;
import model.Airport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AirportDAO {

    public boolean addAirport(Airport airport) {
        String sql = "INSERT INTO airport (airport_code, airport_name, city, country) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, airport.getAirportCode());
            ps.setString(2, airport.getAirportName());
            ps.setString(3, airport.getCity());
            ps.setString(4, airport.getCountry());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Airport: " + e.getMessage());
        }
        return false;
    }

    public List<Airport> getAllAirports() {
        List<Airport> list = new ArrayList<>();
        String sql = "SELECT * FROM airport";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Airport(
                    rs.getInt("airport_id"),
                    rs.getString("airport_code"),
                    rs.getString("airport_name"),
                    rs.getString("city"),
                    rs.getString("country")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Airports: " + e.getMessage());
        }
        return list;
    }

    public Airport searchAirportById(int airportId) {
        String sql = "SELECT * FROM airport WHERE airport_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, airportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Airport(
                        rs.getInt("airport_id"),
                        rs.getString("airport_code"),
                        rs.getString("airport_name"),
                        rs.getString("city"),
                        rs.getString("country")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Airport by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Airport> searchAirportByQuery(String query) {
        List<Airport> list = new ArrayList<>();
        String sql = "SELECT * FROM airport WHERE airport_code LIKE ? OR airport_name LIKE ? OR city LIKE ? OR country LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            ps.setString(4, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Airport(
                        rs.getInt("airport_id"),
                        rs.getString("airport_code"),
                        rs.getString("airport_name"),
                        rs.getString("city"),
                        rs.getString("country")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Airports by query: " + e.getMessage());
        }
        return list;
    }

    public boolean updateAirport(Airport airport) {
        String sql = "UPDATE airport SET airport_code = ?, airport_name = ?, city = ?, country = ? WHERE airport_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, airport.getAirportCode());
            ps.setString(2, airport.getAirportName());
            ps.setString(3, airport.getCity());
            ps.setString(4, airport.getCountry());
            ps.setInt(5, airport.getAirportId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Airport: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteAirport(int airportId) {
        String sql = "DELETE FROM airport WHERE airport_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, airportId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Airport: " + e.getMessage());
        }
        return false;
    }
}
