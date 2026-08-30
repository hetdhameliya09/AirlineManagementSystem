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
        String sql = "INSERT INTO flight (flight_number, airline_name, departure_airport, arrival_airport, departure_time, arrival_time, price, available_seats, aircraft_model, flight_status, departure_terminal, gate_number, duration_minutes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setString(9, flight.getAircraftModel());
            ps.setString(10, flight.getFlightStatus());
            ps.setString(11, flight.getDepartureTerminal());
            ps.setString(12, flight.getGateNumber());
            ps.setInt(13, flight.getDurationMinutes());
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
        } catch (Exception e) {
            System.err.println("Error searching Flights by route: " + e.getMessage());
        }
        return list;
    }

    public List<Flight> searchFlightsByQuery(String query) {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM flight WHERE flight_number LIKE ? OR airline_name LIKE ? OR departure_airport LIKE ? OR arrival_airport LIKE ? OR aircraft_model LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            ps.setString(4, wildcard);
            ps.setString(5, wildcard);
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
        String sql = "UPDATE flight SET flight_number = ?, airline_name = ?, departure_airport = ?, arrival_airport = ?, departure_time = ?, arrival_time = ?, price = ?, available_seats = ?, aircraft_model = ?, flight_status = ?, departure_terminal = ?, gate_number = ?, duration_minutes = ? WHERE flight_id = ?";
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
            ps.setString(9, flight.getAircraftModel());
            ps.setString(10, flight.getFlightStatus());
            ps.setString(11, flight.getDepartureTerminal());
            ps.setString(12, flight.getGateNumber());
            ps.setInt(13, flight.getDurationMinutes());
            ps.setInt(14, flight.getFlightId());
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
        String model = getSafeString(rs, "aircraft_model", "Airbus A320neo");
        String status = getSafeString(rs, "flight_status", "ON_TIME");
        String terminal = getSafeString(rs, "departure_terminal", "T3");
        String gate = getSafeString(rs, "gate_number", "B04");
        int duration = getSafeInt(rs, "duration_minutes", 135);

        return new Flight(
            rs.getInt("flight_id"),
            rs.getString("flight_number"),
            rs.getString("airline_name"),
            rs.getString("departure_airport"),
            rs.getString("arrival_airport"),
            rs.getString("departure_time"),
            rs.getString("arrival_time"),
            rs.getDouble("price"),
            rs.getInt("available_seats"),
            model,
            status,
            terminal,
            gate,
            duration
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

    private int getSafeInt(ResultSet rs, String col, int defaultVal) {
        try {
            int val = rs.getInt(col);
            return val > 0 ? val : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
