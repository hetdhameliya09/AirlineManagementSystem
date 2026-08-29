package dao;

import database.DBConnection;
import model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public Admin login(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("admin_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error during Admin login: " + e.getMessage());
        }
        return null;
    }

    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admin (username, password, name, email) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getName());
            ps.setString(4, admin.getEmail());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error adding Admin: " + e.getMessage());
        }
        return false;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM admin";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Admin(
                    rs.getInt("admin_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Admins: " + e.getMessage());
        }
        return list;
    }

    public Admin searchAdminById(int adminId) {
        String sql = "SELECT * FROM admin WHERE admin_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("admin_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Admin: " + e.getMessage());
        }
        return null;
    }

    public boolean updateAdmin(Admin admin) {
        String sql = "UPDATE admin SET username = ?, password = ?, name = ?, email = ? WHERE admin_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            ps.setString(3, admin.getName());
            ps.setString(4, admin.getEmail());
            ps.setInt(5, admin.getAdminId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Admin: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteAdmin(int adminId) {
        String sql = "DELETE FROM admin WHERE admin_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Admin: " + e.getMessage());
        }
        return false;
    }
}
