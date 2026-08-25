package dao;

import database.DBConnection;
import model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public boolean addTicket(Ticket ticket) {
        String sql = "INSERT INTO ticket (booking_id, passenger_id, ticket_number, seat_number, price, issue_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ticket.getBookingId());
            ps.setInt(2, ticket.getPassengerId());
            ps.setString(3, ticket.getTicketNumber());
            ps.setString(4, ticket.getSeatNumber());
            ps.setDouble(5, ticket.getPrice());
            ps.setString(6, ticket.getIssueDate());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Ticket: " + e.getMessage());
        }
        return false;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT * FROM ticket";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractTicket(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Tickets: " + e.getMessage());
        }
        return list;
    }

    public List<Ticket> getTicketsByBookingId(int bookingId) {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT * FROM ticket WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractTicket(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Tickets by Booking ID: " + e.getMessage());
        }
        return list;
    }

    public Ticket searchTicketById(int ticketId) {
        String sql = "SELECT * FROM ticket WHERE ticket_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractTicket(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Ticket by ID: " + e.getMessage());
        }
        return null;
    }

    public Ticket searchTicketByNumber(String ticketNumber) {
        String sql = "SELECT * FROM ticket WHERE ticket_number = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ticketNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractTicket(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Ticket by Number: " + e.getMessage());
        }
        return null;
    }

    public boolean updateTicket(Ticket ticket) {
        String sql = "UPDATE ticket SET booking_id = ?, passenger_id = ?, ticket_number = ?, seat_number = ?, price = ?, issue_date = ? WHERE ticket_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ticket.getBookingId());
            ps.setInt(2, ticket.getPassengerId());
            ps.setString(3, ticket.getTicketNumber());
            ps.setString(4, ticket.getSeatNumber());
            ps.setDouble(5, ticket.getPrice());
            ps.setString(6, ticket.getIssueDate());
            ps.setInt(7, ticket.getTicketId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Ticket: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteTicket(int ticketId) {
        String sql = "DELETE FROM ticket WHERE ticket_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Ticket: " + e.getMessage());
        }
        return false;
    }

    private Ticket extractTicket(ResultSet rs) throws Exception {
        return new Ticket(
            rs.getInt("ticket_id"),
            rs.getInt("booking_id"),
            rs.getInt("passenger_id"),
            rs.getString("ticket_number"),
            rs.getString("seat_number"),
            rs.getDouble("price"),
            rs.getString("issue_date")
        );
    }
}
