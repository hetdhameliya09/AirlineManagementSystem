package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    private static boolean h2Initialized = false;

    public static Connection getConnection() {
        Connection con = null;
        try {
            String dburl = "jdbc:mysql://localhost:3306/airline?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String dbuser = "root";
            String dbpass = "";
            String driver = "com.mysql.cj.jdbc.Driver";

            Class.forName(driver);
            con = DriverManager.getConnection(dburl, dbuser, dbpass);
            if (con != null) {
                return con;
            }
        } catch (Exception e) {
            // Fallback to H2 in-memory database if MySQL server is not running
        }

        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection("jdbc:h2:mem:airlinedb;DB_CLOSE_DELAY=-1;MODE=MySQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE", "sa", "");
            if (!h2Initialized) {
                synchronized (DBConnection.class) {
                    if (!h2Initialized) {
                        initH2Database(con);
                        h2Initialized = true;
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Database Connection Error (MySQL & H2): " + ex.getMessage());
        }
        return con;
    }

    private static void initH2Database(Connection con) {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS admin (admin_id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(50) NOT NULL, name VARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS customer (customer_id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) NOT NULL UNIQUE, password VARCHAR(50) NOT NULL, name VARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL, phone VARCHAR(20) NOT NULL, passport_number VARCHAR(50) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS airport (airport_id INT PRIMARY KEY AUTO_INCREMENT, airport_code VARCHAR(10) NOT NULL UNIQUE, airport_name VARCHAR(100) NOT NULL, city VARCHAR(50) NOT NULL, country VARCHAR(50) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS flight (flight_id INT PRIMARY KEY AUTO_INCREMENT, flight_number VARCHAR(20) NOT NULL UNIQUE, airline_name VARCHAR(100) NOT NULL, departure_airport VARCHAR(100) NOT NULL, arrival_airport VARCHAR(100) NOT NULL, departure_time VARCHAR(50) NOT NULL, arrival_time VARCHAR(50) NOT NULL, price DOUBLE NOT NULL, available_seats INT NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS booking (booking_id INT PRIMARY KEY AUTO_INCREMENT, customer_id INT NOT NULL, flight_id INT NOT NULL, booking_date DATE NOT NULL, status VARCHAR(20) NOT NULL, total_amount DOUBLE NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS passenger (passenger_id INT PRIMARY KEY AUTO_INCREMENT, booking_id INT NOT NULL, name VARCHAR(100) NOT NULL, age INT NOT NULL, gender VARCHAR(10) NOT NULL, seat_number VARCHAR(10) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS ticket (ticket_id INT PRIMARY KEY AUTO_INCREMENT, booking_id INT NOT NULL, passenger_id INT NOT NULL, ticket_number VARCHAR(50) NOT NULL UNIQUE, seat_number VARCHAR(10) NOT NULL, price DOUBLE NOT NULL, issue_date DATE NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS payment (payment_id INT PRIMARY KEY AUTO_INCREMENT, booking_id INT NOT NULL, amount DOUBLE NOT NULL, payment_date DATE NOT NULL, payment_method VARCHAR(50) NOT NULL, status VARCHAR(20) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS hotel (hotel_id INT PRIMARY KEY AUTO_INCREMENT, hotel_name VARCHAR(100) NOT NULL, city VARCHAR(50) NOT NULL, price_per_night DOUBLE NOT NULL, available_rooms INT NOT NULL, rating DOUBLE NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS hotel_booking (hotel_booking_id INT PRIMARY KEY AUTO_INCREMENT, customer_id INT NOT NULL, hotel_id INT NOT NULL, check_in_date DATE NOT NULL, check_out_date DATE NOT NULL, num_rooms INT NOT NULL, total_price DOUBLE NOT NULL, status VARCHAR(20) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS taxi (taxi_id INT PRIMARY KEY AUTO_INCREMENT, driver_name VARCHAR(100) NOT NULL, phone_number VARCHAR(20) NOT NULL, vehicle_number VARCHAR(20) NOT NULL UNIQUE, vehicle_type VARCHAR(50) NOT NULL, price_per_km DOUBLE NOT NULL, status VARCHAR(20) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS notification (notification_id INT PRIMARY KEY AUTO_INCREMENT, customer_id INT NOT NULL, message TEXT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, status VARCHAR(20) NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS feedback (feedback_id INT PRIMARY KEY AUTO_INCREMENT, customer_id INT NOT NULL, rating INT NOT NULL, comments TEXT NOT NULL, created_at DATE NOT NULL);");

            // Seed initial data if tables are empty
            stmt.execute("MERGE INTO admin (admin_id, username, password, name, email) KEY(admin_id) VALUES (1, 'admin', 'admin123', 'System Administrator', 'admin@airline.com');");
            stmt.execute("MERGE INTO customer (customer_id, username, password, name, email, phone, passport_number) KEY(customer_id) VALUES (1, 'rohan_raj', 'pass123', 'Rohan Raj', 'rohan.raj@email.com', '+91-9876543210', 'K1234567');");
            stmt.execute("MERGE INTO customer (customer_id, username, password, name, email, phone, passport_number) KEY(customer_id) VALUES (2, 'priya_sharma', 'pass123', 'Priya Sharma', 'priya.sharma@email.com', '+91-9876543211', 'K2345678');");
            stmt.execute("MERGE INTO airport (airport_id, airport_code, airport_name, city, country) KEY(airport_id) VALUES (1, 'DEL', 'Indira Gandhi International Airport', 'New Delhi', 'India');");
            stmt.execute("MERGE INTO airport (airport_id, airport_code, airport_name, city, country) KEY(airport_id) VALUES (2, 'BOM', 'Chhatrapati Shivaji Maharaj International Airport', 'Mumbai', 'India');");
            stmt.execute("MERGE INTO airport (airport_id, airport_code, airport_name, city, country) KEY(airport_id) VALUES (3, 'BLR', 'Kempegowda International Airport', 'Bengaluru', 'India');");
            stmt.execute("MERGE INTO flight (flight_id, flight_number, airline_name, departure_airport, arrival_airport, departure_time, arrival_time, price, available_seats) KEY(flight_id) VALUES (1, 'AI-101', 'Air India', 'New Delhi (DEL)', 'Mumbai (BOM)', '2026-09-01 08:00', '2026-09-01 10:15', 5500.0, 160);");
            stmt.execute("MERGE INTO flight (flight_id, flight_number, airline_name, departure_airport, arrival_airport, departure_time, arrival_time, price, available_seats) KEY(flight_id) VALUES (2, '6E-202', 'IndiGo', 'Mumbai (BOM)', 'Bengaluru (BLR)', '2026-09-01 11:30', '2026-09-01 13:15', 4200.0, 140);");
            stmt.execute("MERGE INTO flight (flight_id, flight_number, airline_name, departure_airport, arrival_airport, departure_time, arrival_time, price, available_seats) KEY(flight_id) VALUES (3, 'UK-303', 'Vistara', 'Bengaluru (BLR)', 'New Delhi (DEL)', '2026-09-02 15:00', '2026-09-02 17:45', 6800.0, 115);");
            stmt.execute("MERGE INTO hotel (hotel_id, hotel_name, city, price_per_night, available_rooms, rating) KEY(hotel_id) VALUES (1, 'Taj Mahal Palace', 'Mumbai', 12000.0, 25, 4.9);");
            stmt.execute("MERGE INTO hotel (hotel_id, hotel_name, city, price_per_night, available_rooms, rating) KEY(hotel_id) VALUES (2, 'The Leela Palace', 'New Delhi', 15000.0, 20, 4.8);");
            stmt.execute("MERGE INTO taxi (taxi_id, driver_name, phone_number, vehicle_number, vehicle_type, price_per_km, status) KEY(taxi_id) VALUES (1, 'Ramesh Kumar', '+91-9811122233', 'DL-01-AB-1234', 'Sedan', 15.0, 'AVAILABLE');");
            stmt.execute("MERGE INTO taxi (taxi_id, driver_name, phone_number, vehicle_number, vehicle_type, price_per_km, status) KEY(taxi_id) VALUES (2, 'Suresh Yadav', '+91-9822233344', 'MH-02-CD-5678', 'SUV', 22.0, 'AVAILABLE');");
            stmt.execute("MERGE INTO booking (booking_id, customer_id, flight_id, booking_date, status, total_amount) KEY(booking_id) VALUES (1, 1, 1, '2026-08-25', 'CONFIRMED', 5500.0);");
            stmt.execute("MERGE INTO passenger (passenger_id, booking_id, name, age, gender, seat_number) KEY(passenger_id) VALUES (1, 1, 'Rohan Raj', 28, 'Male', '12A');");
            stmt.execute("MERGE INTO ticket (ticket_id, booking_id, passenger_id, ticket_number, seat_number, price, issue_date) KEY(ticket_id) VALUES (1, 1, 1, 'TKT-10001', '12A', 5500.0, '2026-08-25');");
            stmt.execute("MERGE INTO notification (notification_id, customer_id, message, created_at, status) KEY(notification_id) VALUES (1, 1, 'Welcome to SkyWays Airline Portal! Flight AI-101 is confirmed.', CURRENT_TIMESTAMP, 'UNREAD');");
            stmt.execute("MERGE INTO feedback (feedback_id, customer_id, rating, comments, created_at) KEY(feedback_id) VALUES (1, 1, 5, 'Excellent service and smooth flight booking experience!', '2026-08-26');");
        } catch (Exception e) {
            System.err.println("Error initializing H2 DB: " + e.getMessage());
        }
    }
}

