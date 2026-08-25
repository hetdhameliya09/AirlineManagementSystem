CREATE DATABASE IF NOT EXISTS airline;
USE airline;

CREATE TABLE IF NOT EXISTS admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    passport_number VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS airport (
    airport_id INT PRIMARY KEY AUTO_INCREMENT,
    airport_code VARCHAR(10) NOT NULL UNIQUE,
    airport_name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS flight (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    flight_number VARCHAR(20) NOT NULL UNIQUE,
    airline_name VARCHAR(100) NOT NULL,
    departure_airport VARCHAR(100) NOT NULL,
    arrival_airport VARCHAR(100) NOT NULL,
    departure_time VARCHAR(50) NOT NULL,
    arrival_time VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    available_seats INT NOT NULL
);

CREATE TABLE IF NOT EXISTS booking (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount DOUBLE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS passenger (
    passenger_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ticket (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    passenger_id INT NOT NULL,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    seat_number VARCHAR(10) NOT NULL,
    price DOUBLE NOT NULL,
    issue_date DATE NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hotel (
    hotel_id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    price_per_night DOUBLE NOT NULL,
    available_rooms INT NOT NULL,
    rating DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS hotel_booking (
    hotel_booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    hotel_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    num_rooms INT NOT NULL,
    total_price DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (hotel_id) REFERENCES hotel(hotel_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS taxi (
    taxi_id INT PRIMARY KEY AUTO_INCREMENT,
    driver_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,
    price_per_km DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS notification (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    rating INT NOT NULL,
    comments TEXT NOT NULL,
    created_at DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE
);

-- Clean old sample data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE admin;
TRUNCATE TABLE customer;
TRUNCATE TABLE airport;
TRUNCATE TABLE flight;
TRUNCATE TABLE booking;
TRUNCATE TABLE passenger;
TRUNCATE TABLE ticket;
TRUNCATE TABLE payment;
TRUNCATE TABLE hotel;
TRUNCATE TABLE hotel_booking;
TRUNCATE TABLE taxi;
TRUNCATE TABLE notification;
TRUNCATE TABLE feedback;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Admin Seed
INSERT INTO admin (admin_id, username, password, name, email) VALUES
(1, 'admin', 'admin123', 'System Administrator', 'admin@airline.com');

-- 2. 5 Customers Seed
INSERT INTO customer (customer_id, username, password, name, email, phone, passport_number) VALUES
(1, 'rohan_raj', 'pass123', 'Rohan Raj', 'rohan.raj@email.com', '+91-9876543210', 'K1234567'),
(2, 'priya_sharma', 'pass123', 'Priya Sharma', 'priya.sharma@email.com', '+91-9876543211', 'K2345678'),
(3, 'aarav_gupta', 'pass123', 'Aarav Gupta', 'aarav.gupta@email.com', '+91-9876543212', 'K3456789'),
(4, 'ananya_verma', 'pass123', 'Ananya Verma', 'ananya.verma@email.com', '+91-9876543213', 'K4567890'),
(5, 'vikram_singh', 'pass123', 'Vikram Singh', 'vikram.singh@email.com', '+91-9876543214', 'K5678901');

-- 3. Airports Seed
INSERT INTO airport (airport_id, airport_code, airport_name, city, country) VALUES
(1, 'DEL', 'Indira Gandhi International Airport', 'New Delhi', 'India'),
(2, 'BOM', 'Chhatrapati Shivaji Maharaj International Airport', 'Mumbai', 'India'),
(3, 'BLR', 'Kempegowda International Airport', 'Bengaluru', 'India'),
(4, 'MAA', 'Chennai International Airport', 'Chennai', 'India'),
(5, 'CCU', 'Netaji Subhash Chandra Bose International Airport', 'Kolkata', 'India');

-- 4. Flights Seed
INSERT INTO flight (flight_id, flight_number, airline_name, departure_airport, arrival_airport, departure_time, arrival_time, price, available_seats) VALUES
(1, 'AI-101', 'Air India', 'New Delhi (DEL)', 'Mumbai (BOM)', '2026-08-10 08:00', '2026-08-10 10:15', 5500.0, 160),
(2, '6E-202', 'IndiGo', 'Mumbai (BOM)', 'Bengaluru (BLR)', '2026-08-10 11:30', '2026-08-10 13:15', 4200.0, 140),
(3, 'UK-303', 'Vistara', 'Bengaluru (BLR)', 'New Delhi (DEL)', '2026-08-11 15:00', '2026-08-11 17:45', 6800.0, 115),
(4, 'SG-404', 'SpiceJet', 'New Delhi (DEL)', 'Kolkata (CCU)', '2026-08-11 09:15', '2026-08-11 11:30', 3800.0, 135),
(5, 'AI-505', 'Air India', 'Mumbai (BOM)', 'Chennai (MAA)', '2026-08-12 06:45', '2026-08-12 08:45', 4900.0, 150);

-- 5. Hotels Seed
INSERT INTO hotel (hotel_id, hotel_name, city, price_per_night, available_rooms, rating) VALUES
(1, 'Taj Mahal Palace', 'Mumbai', 12000.0, 25, 4.9),
(2, 'The Leela Palace', 'New Delhi', 15000.0, 20, 4.8),
(3, 'The Oberoi', 'Bengaluru', 11000.0, 30, 4.7);

-- 6. Taxis Seed
INSERT INTO taxi (taxi_id, driver_name, phone_number, vehicle_number, vehicle_type, price_per_km, status) VALUES
(1, 'Ramesh Kumar', '+91-9811122233', 'DL-01-AB-1234', 'Sedan', 15.0, 'AVAILABLE'),
(2, 'Suresh Yadav', '+91-9822233344', 'MH-02-CD-5678', 'SUV', 22.0, 'AVAILABLE');

-- 7. Bookings Seed
INSERT INTO booking (booking_id, customer_id, flight_id, booking_date, status, total_amount) VALUES
(1, 1, 1, '2026-08-01', 'CONFIRMED', 5500.0),
(2, 1, 3, '2026-08-02', 'CONFIRMED', 6800.0),
(3, 2, 2, '2026-08-01', 'CONFIRMED', 4200.0),
(4, 3, 1, '2026-08-01', 'CONFIRMED', 5500.0),
(5, 4, 3, '2026-08-02', 'CONFIRMED', 6800.0),
(6, 5, 2, '2026-08-01', 'CONFIRMED', 4200.0),
(7, 5, 5, '2026-08-03', 'CONFIRMED', 4900.0);

-- 8. Passengers Seed
INSERT INTO passenger (passenger_id, booking_id, name, age, gender, seat_number) VALUES
(1, 1, 'Rohan Raj', 28, 'Male', '12A'),
(2, 2, 'Rohan Raj', 28, 'Male', '14C'),
(3, 3, 'Priya Sharma', 26, 'Female', '08F'),
(4, 4, 'Aarav Gupta', 32, 'Male', '05B'),
(5, 5, 'Ananya Verma', 24, 'Female', '03A'),
(6, 6, 'Vikram Singh', 35, 'Male', '11D'),
(7, 7, 'Vikram Singh', 35, 'Male', '18F');

-- 9. Tickets Seed
INSERT INTO ticket (ticket_id, booking_id, passenger_id, ticket_number, seat_number, price, issue_date) VALUES
(1, 1, 1, 'TKT-10001', '12A', 5500.0, '2026-08-01'),
(2, 2, 2, 'TKT-10002', '14C', 6800.0, '2026-08-02'),
(3, 3, 3, 'TKT-10003', '08F', 4200.0, '2026-08-01'),
(4, 4, 4, 'TKT-10004', '05B', 5500.0, '2026-08-01'),
(5, 5, 5, 'TKT-10005', '03A', 6800.0, '2026-08-02'),
(6, 6, 6, 'TKT-10006', '11D', 4200.0, '2026-08-01'),
(7, 7, 7, 'TKT-10007', '18F', 4900.0, '2026-08-03');

-- 10. Payments Seed
INSERT INTO payment (payment_id, booking_id, amount, payment_date, payment_method, status) VALUES
(1, 1, 5500.0, '2026-08-01', 'UPI', 'COMPLETED'),
(2, 2, 6800.0, '2026-08-02', 'Credit Card', 'COMPLETED'),
(3, 3, 4200.0, '2026-08-01', 'Debit Card', 'COMPLETED'),
(4, 4, 5500.0, '2026-08-01', 'UPI', 'COMPLETED'),
(5, 5, 6800.0, '2026-08-02', 'UPI', 'COMPLETED'),
(6, 6, 4200.0, '2026-08-01', 'Credit Card', 'COMPLETED'),
(7, 7, 4900.0, '2026-08-03', 'NetBanking', 'COMPLETED');

-- 11. Notifications Seed
INSERT INTO notification (notification_id, customer_id, message, created_at, status) VALUES
(1, 1, 'Flight AI-101 booking confirmed. Ticket: TKT-10001', '2026-08-01 10:00:00', 'UNREAD'),
(2, 2, 'Flight 6E-202 booking confirmed. Ticket: TKT-10003', '2026-08-01 11:30:00', 'UNREAD');

-- 13. Feedback Seed
INSERT INTO feedback (feedback_id, customer_id, rating, comments, created_at) VALUES
(1, 1, 5, 'Excellent service and smooth flight booking experience!', '2026-08-02'),
(2, 2, 4, 'Great system, easy ticket generation.', '2026-08-02');
