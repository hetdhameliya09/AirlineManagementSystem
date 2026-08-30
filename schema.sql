CREATE DATABASE IF NOT EXISTS airline;
USE airline;

CREATE TABLE IF NOT EXISTS admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
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
    available_seats INT NOT NULL,
    aircraft_model VARCHAR(50) DEFAULT 'Airbus A320neo',
    flight_status VARCHAR(20) DEFAULT 'ON_TIME',
    departure_terminal VARCHAR(20) DEFAULT 'T3',
    gate_number VARCHAR(20) DEFAULT 'B04',
    duration_minutes INT DEFAULT 135
);

CREATE TABLE IF NOT EXISTS booking (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount DOUBLE NOT NULL,
    pnr_code VARCHAR(20) UNIQUE,
    cabin_class VARCHAR(30) DEFAULT 'Economy',
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
    nationality VARCHAR(50) DEFAULT 'Indian',
    meal_preference VARCHAR(50) DEFAULT 'Standard',
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

-- 2. Customers Seed
INSERT INTO customer (customer_id, username, password, name, email, phone, passport_number) VALUES
(1, 'rohan_raj', 'pass123', 'Rohan Raj', 'rohan.raj@email.com', '+91-9876543210', 'K1234567'),
(2, 'priya_sharma', 'pass123', 'Priya Sharma', 'priya.sharma@email.com', '+91-9876543211', 'K2345678');

-- 3. International Airports Seed
INSERT INTO airport (airport_id, airport_code, airport_name, city, country) VALUES
(1, 'DEL', 'Indira Gandhi International Airport', 'New Delhi', 'India'),
(2, 'BOM', 'Chhatrapati Shivaji Maharaj International Airport', 'Mumbai', 'India'),
(3, 'BLR', 'Kempegowda International Airport', 'Bengaluru', 'India'),
(4, 'DXB', 'Dubai International Airport', 'Dubai', 'UAE'),
(5, 'LHR', 'London Heathrow Airport', 'London', 'UK'),
(6, 'JFK', 'John F. Kennedy International Airport', 'New York', 'USA');

-- 4. Real International Flights Seed
INSERT INTO flight (flight_id, flight_number, airline_name, departure_airport, arrival_airport, departure_time, arrival_time, price, available_seats, aircraft_model, flight_status, departure_terminal, gate_number, duration_minutes) VALUES
(1, 'AI-101', 'Air India', 'New Delhi (DEL)', 'Mumbai (BOM)', '2026-09-01 08:00', '2026-09-01 10:15', 5500.0, 160, 'Boeing 787 Dreamliner', 'ON_TIME', 'T3', 'A12', 135),
(2, 'EK-501', 'Emirates', 'Mumbai (BOM)', 'Dubai (DXB)', '2026-09-01 14:30', '2026-09-01 16:45', 18500.0, 220, 'Airbus A380', 'BOARDING', 'T2', 'B08', 225),
(3, 'BA-142', 'British Airways', 'New Delhi (DEL)', 'London (LHR)', '2026-09-02 02:15', '2026-09-02 07:30', 42000.0, 185, 'Boeing 777-300ER', 'ON_TIME', 'T3', 'C15', 525),
(4, '6E-202', 'IndiGo', 'Mumbai (BOM)', 'Bengaluru (BLR)', '2026-09-01 11:30', '2026-09-01 13:15', 4200.0, 140, 'Airbus A320neo', 'ON_TIME', 'T1', '14', 105),
(5, 'SQ-421', 'Singapore Airlines', 'Bengaluru (BLR)', 'Singapore (SIN)', '2026-09-02 23:10', '2026-09-03 06:15', 24500.0, 190, 'Airbus A350-900', 'ON_TIME', 'T2', 'G03', 275);

-- 5. Hotels Seed
INSERT INTO hotel (hotel_id, hotel_name, city, price_per_night, available_rooms, rating) VALUES
(1, 'Taj Mahal Palace', 'Mumbai', 12000.0, 25, 4.9),
(2, 'Burj Al Arab', 'Dubai', 35000.0, 10, 5.0);

-- 6. Taxis Seed
INSERT INTO taxi (taxi_id, driver_name, phone_number, vehicle_number, vehicle_type, price_per_km, status) VALUES
(1, 'Ramesh Kumar', '+91-9811122233', 'DL-01-AB-1234', 'Sedan', 15.0, 'AVAILABLE'),
(2, 'Suresh Yadav', '+91-9822233344', 'MH-02-CD-5678', 'SUV', 22.0, 'AVAILABLE');

-- 7. Bookings Seed with PNR
INSERT INTO booking (booking_id, customer_id, flight_id, booking_date, status, total_amount, pnr_code, cabin_class) VALUES
(1, 1, 1, '2026-08-25', 'CONFIRMED', 5500.0, 'SKY7X9', 'Economy');

-- 8. Passengers Seed
INSERT INTO passenger (passenger_id, booking_id, name, age, gender, seat_number, nationality, meal_preference) VALUES
(1, 1, 'Rohan Raj', 28, 'Male', '12A', 'Indian', 'Vegetarian');

-- 9. Tickets Seed
INSERT INTO ticket (ticket_id, booking_id, passenger_id, ticket_number, seat_number, price, issue_date) VALUES
(1, 1, 1, 'TKT-10001', '12A', 5500.0, '2026-08-25');

-- 10. Payments Seed
INSERT INTO payment (payment_id, booking_id, amount, payment_date, payment_method, status) VALUES
(1, 1, 5500.0, '2026-08-25', 'UPI', 'COMPLETED');

-- 11. Notifications Seed
INSERT INTO notification (notification_id, customer_id, message, created_at, status) VALUES
(1, 1, 'Welcome to SkyWays Airline Portal! Flight AI-101 is confirmed with PNR SKY7X9.', CURRENT_TIMESTAMP, 'UNREAD');

-- 13. Feedback Seed
INSERT INTO feedback (feedback_id, customer_id, rating, comments, created_at) VALUES
(1, 1, 5, 'Excellent international flight booking experience!', '2026-08-26');
