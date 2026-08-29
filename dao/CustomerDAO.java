package dao;

import database.DBConnection;
import model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public Customer login(String username, String password) {
        String sql = "SELECT * FROM customer WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("passport_number")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error during Customer login: " + e.getMessage());
        }
        return null;
    }

    public boolean addCustomer(Customer customer) {
        String sql = "{call sp_add_customer(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = DBConnection.getConnection();
             java.sql.CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, customer.getUsername());
            cs.setString(2, customer.getPassword());
            cs.setString(3, customer.getName());
            cs.setString(4, customer.getEmail());
            cs.setString(5, customer.getPhone());
            cs.setString(6, customer.getPassportNumber());
            cs.registerOutParameter(7, java.sql.Types.INTEGER);
            cs.execute();
            int newId = cs.getInt(7);
            if (newId > 0) {
                customer.setCustomerId(newId);
                return true;
            }
        } catch (Exception e) {
            String insertSql = "INSERT INTO customer (username, password, name, email, phone, passport_number) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, customer.getUsername());
                ps.setString(2, customer.getPassword());
                ps.setString(3, customer.getName());
                ps.setString(4, customer.getEmail());
                ps.setString(5, customer.getPhone());
                ps.setString(6, customer.getPassportNumber());
                return ps.executeUpdate() > 0;
            } catch (Exception ex) {
                System.err.println("Error adding Customer: " + ex.getMessage());
            }
        }
        return false;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("passport_number")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching Customers: " + e.getMessage());
        }
        return list;
    }

    public Customer searchCustomerById(int customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("passport_number")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching Customer: " + e.getMessage());
        }
        return null;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customer SET username = ?, password = ?, name = ?, email = ?, phone = ?, passport_number = ? WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getUsername());
            ps.setString(2, customer.getPassword());
            ps.setString(3, customer.getName());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getPhone());
            ps.setString(6, customer.getPassportNumber());
            ps.setInt(7, customer.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating Customer: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting Customer: " + e.getMessage());
        }
        return false;
    }
}
