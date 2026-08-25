package dao;

import database.DBConnection;
import model.Feedback;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public boolean addFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedback (customer_id, rating, comments, created_at) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, feedback.getCustomerId());
            ps.setInt(2, feedback.getRating());
            ps.setString(3, feedback.getComments());
            ps.setString(4, feedback.getCreatedAt());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Feedback: " + e.getMessage());
        }
        return false;
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractFeedback(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Feedback: " + e.getMessage());
        }
        return list;
    }

    public List<Feedback> getFeedbackByCustomerId(int customerId) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFeedback(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Customer Feedback: " + e.getMessage());
        }
        return list;
    }

    public Feedback searchFeedbackById(int feedbackId) {
        String sql = "SELECT * FROM feedback WHERE feedback_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFeedback(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Feedback by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateFeedback(Feedback feedback) {
        String sql = "UPDATE feedback SET customer_id = ?, rating = ?, comments = ?, created_at = ? WHERE feedback_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, feedback.getCustomerId());
            ps.setInt(2, feedback.getRating());
            ps.setString(3, feedback.getComments());
            ps.setString(4, feedback.getCreatedAt());
            ps.setInt(5, feedback.getFeedbackId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Feedback: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteFeedback(int feedbackId) {
        String sql = "DELETE FROM feedback WHERE feedback_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Feedback: " + e.getMessage());
        }
        return false;
    }

    private Feedback extractFeedback(ResultSet rs) throws Exception {
        return new Feedback(
            rs.getInt("feedback_id"),
            rs.getInt("customer_id"),
            rs.getInt("rating"),
            rs.getString("comments"),
            rs.getString("created_at")
        );
    }
}
