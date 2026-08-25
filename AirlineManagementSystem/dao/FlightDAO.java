package dao;

import database.DBConnection;
import model.Flight;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public boolean addFlight(Flight flight) {
        String sql = "INSERT INTO flight (flight_number, airline_name, departure_airport, arrival_airport, departure_time, arrival_time, price, available_seats) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirlineName());
            ps.setString(3, flight.getDepartureAirport());
            ps.setString(4, flight.getArrivalAirport());
            ps.setString(5, flight.getDepartureTime());
            ps.setString(6, flight.getArrivalTime());
            ps.setDouble(7, flight.getPrice());
            ps.setInt(8, flight.getAvailableSeats());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Flight: " + e.getMessage());
        }
        return false;
    }

    public List<Flight> getAllFlights() {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flight";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractFlight(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Flights: " + e.getMessage());
        }
        return list;
    }

    public Flight searchFlightById(int flightId) {
        String sql = "SELECT * FROM flight WHERE flight_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFlight(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Flight by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Flight> searchFlightsByRoute(String departure, String arrival) {
        List<Flight> list = new ArrayList<>();
        String procSql = "{call sp_search_flights(?, ?)}";
        try (Connection con = DBConnection.getConnection();
             java.sql.CallableStatement cs = con.prepareCall(procSql)) {
            cs.setString(1, departure);
            cs.setString(2, arrival);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFlight(rs));
                }
                return list;
            }
        } catch (Exception e) {
            String sql = "SELECT * FROM flight WHERE departure_airport LIKE ? AND arrival_airport LIKE ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, "%" + departure + "%");
                ps.setString(2, "%" + arrival + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(extractFlight(rs));
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error searching Flights by route: " + ex.getMessage());
            }
        }
        return list;
    }

    public List<Flight> searchFlightsByQuery(String query) {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flight WHERE flight_number LIKE ? OR airline_name LIKE ? OR departure_airport LIKE ? OR arrival_airport LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            ps.setString(4, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFlight(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Flights by query: " + e.getMessage());
        }
        return list;
    }

    public boolean updateFlight(Flight flight) {
        String sql = "UPDATE flight SET flight_number = ?, airline_name = ?, departure_airport = ?, arrival_airport = ?, departure_time = ?, arrival_time = ?, price = ?, available_seats = ? WHERE flight_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirlineName());
            ps.setString(3, flight.getDepartureAirport());
            ps.setString(4, flight.getArrivalAirport());
            ps.setString(5, flight.getDepartureTime());
            ps.setString(6, flight.getArrivalTime());
            ps.setDouble(7, flight.getPrice());
            ps.setInt(8, flight.getAvailableSeats());
            ps.setInt(9, flight.getFlightId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Flight: " + e.getMessage());
        }
        return false;
    }

    public boolean updateAvailableSeats(int flightId, int seatsToDeduct) {
        String sql = "UPDATE flight SET available_seats = available_seats - ? WHERE flight_id = ? AND available_seats >= ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, seatsToDeduct);
            ps.setInt(2, flightId);
            ps.setInt(3, seatsToDeduct);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating available seats: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteFlight(int flightId) {
        String sql = "DELETE FROM flight WHERE flight_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Flight: " + e.getMessage());
        }
        return false;
    }

    private Flight extractFlight(ResultSet rs) throws Exception {
        return new Flight(
            rs.getInt("flight_id"),
            rs.getString("flight_number"),
            rs.getString("airline_name"),
            rs.getString("departure_airport"),
            rs.getString("arrival_airport"),
            rs.getString("departure_time"),
            rs.getString("arrival_time"),
            rs.getDouble("price"),
            rs.getInt("available_seats")
        );
    }
}
