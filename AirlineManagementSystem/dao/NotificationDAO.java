package dao;

import database.DBConnection;
import model.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public boolean addNotification(Notification notification) {
        String sql = "INSERT INTO notification (customer_id, message, created_at, status) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notification.getCustomerId());
            ps.setString(2, notification.getMessage());
            ps.setString(3, notification.getCreatedAt());
            ps.setString(4, notification.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Notification: " + e.getMessage());
        }
        return false;
    }

    public List<Notification> getAllNotifications() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractNotification(rs));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Notifications: " + e.getMessage());
        }
        return list;
    }

    public List<Notification> getNotificationsByCustomerId(int customerId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractNotification(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching Customer Notifications: " + e.getMessage());
        }
        return list;
    }

    public Notification searchNotificationById(int notificationId) {
        String sql = "SELECT * FROM notification WHERE notification_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractNotification(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Notification by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateNotification(Notification notification) {
        String sql = "UPDATE notification SET customer_id = ?, message = ?, created_at = ?, status = ? WHERE notification_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notification.getCustomerId());
            ps.setString(2, notification.getMessage());
            ps.setString(3, notification.getCreatedAt());
            ps.setString(4, notification.getStatus());
            ps.setInt(5, notification.getNotificationId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Notification: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM notification WHERE notification_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Notification: " + e.getMessage());
        }
        return false;
    }

    private Notification extractNotification(ResultSet rs) throws Exception {
        return new Notification(
            rs.getInt("notification_id"),
            rs.getInt("customer_id"),
            rs.getString("message"),
            rs.getString("created_at"),
            rs.getString("status")
        );
    }
}
