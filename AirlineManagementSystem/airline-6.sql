-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Aug 06, 2026 at 07:08 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `airline`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_book_flight` (IN `p_customer_id` INT, IN `p_flight_id` INT, IN `p_passenger_name` VARCHAR(100), IN `p_passenger_age` INT, IN `p_passenger_gender` VARCHAR(10), IN `p_seat_number` VARCHAR(10), IN `p_payment_method` VARCHAR(50), OUT `p_booking_id` INT, OUT `p_ticket_number` VARCHAR(50), OUT `p_message` VARCHAR(255))   proc_label: BEGIN
    DECLARE v_price DOUBLE;
    DECLARE v_seats INT;
    DECLARE v_passenger_id INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_booking_id = 0;
        SET p_ticket_number = '';
        SET p_message = 'Booking failed due to a database error.';
    END;

    START TRANSACTION;

    
    SELECT price, available_seats INTO v_price, v_seats 
    FROM flight WHERE flight_id = p_flight_id FOR UPDATE;

    IF v_seats IS NULL THEN
        SET p_booking_id = 0;
        SET p_message = 'Error: Flight not found.';
        ROLLBACK;
        LEAVE proc_label;
    END IF;

    IF v_seats <= 0 THEN
        SET p_booking_id = 0;
        SET p_message = 'Error: No available seats on this flight.';
        ROLLBACK;
        LEAVE proc_label;
    END IF;

    
    INSERT INTO booking (customer_id, flight_id, booking_date, status, total_amount)
    VALUES (p_customer_id, p_flight_id, CURDATE(), 'CONFIRMED', v_price);
    
    SET p_booking_id = LAST_INSERT_ID();

    
    INSERT INTO passenger (booking_id, name, age, gender, seat_number)
    VALUES (p_booking_id, p_passenger_name, p_passenger_age, p_passenger_gender, p_seat_number);
    
    SET v_passenger_id = LAST_INSERT_ID();

    
    SET p_ticket_number = CONCAT('TKT-', UNIX_TIMESTAMP(), '-', p_booking_id);
    INSERT INTO ticket (booking_id, passenger_id, ticket_number, seat_number, price, issue_date)
    VALUES (p_booking_id, v_passenger_id, p_ticket_number, p_seat_number, v_price, CURDATE());

    
    INSERT INTO payment (booking_id, amount, payment_date, payment_method, status)
    VALUES (p_booking_id, v_price, CURDATE(), p_payment_method, 'COMPLETED');

    
    INSERT INTO notification (customer_id, message, created_at, status)
    VALUES (p_customer_id, CONCAT('Flight booking successful! Ticket No: ', p_ticket_number), NOW(), 'UNREAD');

    COMMIT;
    SET p_message = 'Booking successfully completed!';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_cancel_booking` (IN `p_booking_id` INT, OUT `p_status` VARCHAR(50), OUT `p_message` VARCHAR(255))   proc_label: BEGIN
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_flight_id INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Cancellation failed due to a database error.';
    END;

    START TRANSACTION;

    SELECT status, flight_id INTO v_current_status, v_flight_id 
    FROM booking WHERE booking_id = p_booking_id FOR UPDATE;

    IF v_current_status IS NULL THEN
        SET p_status = 'NOT_FOUND';
        SET p_message = 'Error: Booking ID does not exist.';
        ROLLBACK;
        LEAVE proc_label;
    END IF;

    IF v_current_status = 'CANCELLED' THEN
        SET p_status = 'ALREADY_CANCELLED';
        SET p_message = 'Notice: Booking is already cancelled.';
        ROLLBACK;
        LEAVE proc_label;
    END IF;

    
    UPDATE booking SET status = 'CANCELLED' WHERE booking_id = p_booking_id;

    
    UPDATE payment SET status = 'REFUNDED' WHERE booking_id = p_booking_id;

    
    UPDATE flight SET available_seats = available_seats + 1 WHERE flight_id = v_flight_id;

    COMMIT;
    SET p_status = 'SUCCESS';
    SET p_message = 'Booking cancelled successfully and seat restored.';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_customer_report` (IN `p_customer_id` INT)   BEGIN
    
    SELECT customer_id, username, name, email, phone, passport_number 
    FROM customer WHERE customer_id = p_customer_id;

    
    SELECT b.booking_id, f.flight_number, f.airline_name, f.departure_airport, f.arrival_airport, b.booking_date, b.status, b.total_amount
    FROM booking b
    JOIN flight f ON b.flight_id = f.flight_id
    WHERE b.customer_id = p_customer_id;

    
    SELECT hb.hotel_booking_id, h.hotel_name, h.city, hb.check_in_date, hb.check_out_date, hb.total_price, hb.status
    FROM hotel_booking hb
    JOIN hotel h ON hb.hotel_id = h.hotel_id
    WHERE hb.customer_id = p_customer_id;

    
    SELECT fn_get_total_spent_by_customer(p_customer_id) AS grand_total_spent;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_search_flights` (IN `p_origin` VARCHAR(100), IN `p_destination` VARCHAR(100))   BEGIN
    SELECT 
        flight_id,
        flight_number,
        airline_name,
        departure_airport,
        arrival_airport,
        departure_time,
        arrival_time,
        price,
        available_seats
    FROM flight
    WHERE (p_origin IS NULL OR p_origin = '' OR departure_airport LIKE CONCAT('%', p_origin, '%'))
      AND (p_destination IS NULL OR p_destination = '' OR arrival_airport LIKE CONCAT('%', p_destination, '%'))
      AND available_seats > 0
    ORDER BY price ASC;
END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `fn_get_total_spent_by_customer` (`p_customer_id` INT) RETURNS DOUBLE DETERMINISTIC READS SQL DATA BEGIN
    DECLARE total_flight DOUBLE DEFAULT 0;
    DECLARE total_hotel DOUBLE DEFAULT 0;
    
    SELECT COALESCE(SUM(total_amount), 0) INTO total_flight 
    FROM booking 
    WHERE customer_id = p_customer_id AND status = 'CONFIRMED';
    
    SELECT COALESCE(SUM(total_price), 0) INTO total_hotel 
    FROM hotel_booking 
    WHERE customer_id = p_customer_id AND status = 'CONFIRMED';
    
    RETURN (total_flight + total_hotel);
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `fn_is_seat_available` (`p_flight_id` INT, `p_requested_seats` INT) RETURNS TINYINT(1) DETERMINISTIC READS SQL DATA BEGIN
    DECLARE seats INT DEFAULT 0;
    
    SELECT available_seats INTO seats 
    FROM flight 
    WHERE flight_id = p_flight_id;
    
    IF seats >= p_requested_seats THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `admin_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`admin_id`, `username`, `password`, `name`, `email`) VALUES
(1, 'admin', 'admin123', 'System Administrator', 'admin@airline.com');

--
-- Triggers `admin`
--
DELIMITER $$
CREATE TRIGGER `trg_admin_before_insert` BEFORE INSERT ON `admin` FOR EACH ROW BEGIN
    IF EXISTS (SELECT 1 FROM admin WHERE username = NEW.username) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Admin Username';
    END IF;
    IF NEW.password IS NULL OR TRIM(NEW.password) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blank Password';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `airport`
--

CREATE TABLE `airport` (
  `airport_id` int(11) NOT NULL,
  `airport_code` varchar(10) NOT NULL,
  `airport_name` varchar(100) NOT NULL,
  `city` varchar(50) NOT NULL,
  `country` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `airport`
--

INSERT INTO `airport` (`airport_id`, `airport_code`, `airport_name`, `city`, `country`) VALUES
(1, 'DEL', 'Indira Gandhi International Airport', 'New Delhi', 'India'),
(2, 'BOM', 'Chhatrapati Shivaji Maharaj International Airport', 'Mumbai', 'India'),
(3, 'BLR', 'Kempegowda International Airport', 'Bengaluru', 'India'),
(4, 'MAA', 'Chennai International Airport', 'Chennai', 'India'),
(5, 'CCU', 'Netaji Subhash Chandra Bose International Airport', 'Kolkata', 'India');

--
-- Triggers `airport`
--
DELIMITER $$
CREATE TRIGGER `trg_airport_before_insert` BEFORE INSERT ON `airport` FOR EACH ROW BEGIN
    IF EXISTS (SELECT 1 FROM airport WHERE airport_code = NEW.airport_code) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Airport Code';
    END IF;
    IF NEW.airport_code NOT REGEXP '^[A-Z]{3}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Airport Code';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

CREATE TABLE `booking` (
  `booking_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL,
  `booking_date` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL,
  `total_amount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `booking`
--

INSERT INTO `booking` (`booking_id`, `customer_id`, `flight_id`, `booking_date`, `status`, `total_amount`) VALUES
(1, 1, 1, '2026-08-01', 'CONFIRMED', 5500),
(2, 1, 3, '2026-08-02', 'CONFIRMED', 6800),
(3, 2, 2, '2026-08-01', 'CONFIRMED', 4200),
(4, 3, 1, '2026-08-01', 'CONFIRMED', 5500),
(5, 4, 3, '2026-08-02', 'CONFIRMED', 6800),
(6, 5, 2, '2026-08-01', 'CONFIRMED', 4200),
(7, 5, 5, '2026-08-03', 'CONFIRMED', 4900);

--
-- Triggers `booking`
--
DELIMITER $$
CREATE TRIGGER `trg_booking_after_delete` AFTER DELETE ON `booking` FOR EACH ROW BEGIN
    UPDATE flight 
    SET available_seats = available_seats + 1 
    WHERE flight_id = OLD.flight_id;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_booking_after_insert` AFTER INSERT ON `booking` FOR EACH ROW BEGIN
    UPDATE flight 
    SET available_seats = available_seats - 1 
    WHERE flight_id = NEW.flight_id;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_booking_before_insert` BEFORE INSERT ON `booking` FOR EACH ROW BEGIN
    DECLARE seats INT;
    SELECT available_seats INTO seats FROM flight WHERE flight_id = NEW.flight_id;
    IF seats IS NULL OR seats <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No Available Seats';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `customer_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `passport_number` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customer_id`, `username`, `password`, `name`, `email`, `phone`, `passport_number`) VALUES
(1, 'rohan_raj', 'pass123', 'Rohan Raj', 'rohan.raj@email.com', '+91-9876543210', 'K1234567'),
(2, 'priya_sharma', 'pass123', 'Priya Sharma', 'priya.sharma@email.com', '+91-9876543211', 'K2345678'),
(3, 'aarav_gupta', 'pass123', 'Aarav Gupta', 'aarav.gupta@email.com', '+91-9876543212', 'K3456789'),
(4, 'ananya_verma', 'pass123', 'Ananya Verma', 'ananya.verma@email.com', '+91-9876543213', 'K4567890'),
(5, 'vikram_singh', 'pass123', 'Vikram Singh', 'vikram.singh@email.com', '+91-9876543214', 'K5678901');

--
-- Triggers `customer`
--
DELIMITER $$
CREATE TRIGGER `trg_customer_before_insert` BEFORE INSERT ON `customer` FOR EACH ROW BEGIN
    IF EXISTS (SELECT 1 FROM customer WHERE email = NEW.email) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Email';
    END IF;
    IF EXISTS (SELECT 1 FROM customer WHERE username = NEW.username) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Username';
    END IF;
    IF NEW.name IS NULL OR TRIM(NEW.name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blank Customer Name';
    END IF;
    IF NEW.phone IS NOT NULL AND NEW.phone NOT REGEXP '^[0-9+ -]{10,20}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Phone Number';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `feedback`
--

CREATE TABLE `feedback` (
  `feedback_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL,
  `comments` text NOT NULL,
  `created_at` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `feedback`
--

INSERT INTO `feedback` (`feedback_id`, `customer_id`, `rating`, `comments`, `created_at`) VALUES
(1, 1, 5, 'Excellent service and smooth flight booking experience!', '2026-08-02'),
(2, 2, 4, 'Great system, easy ticket generation.', '2026-08-02');

--
-- Triggers `feedback`
--
DELIMITER $$
CREATE TRIGGER `trg_feedback_before_insert` BEFORE INSERT ON `feedback` FOR EACH ROW BEGIN
    IF NEW.rating < 1 OR NEW.rating > 5 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Rating';
    END IF;
    IF NEW.comments IS NULL OR TRIM(NEW.comments) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blank Feedback Comments';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `flight`
--

CREATE TABLE `flight` (
  `flight_id` int(11) NOT NULL,
  `flight_number` varchar(20) NOT NULL,
  `airline_name` varchar(100) NOT NULL,
  `departure_airport` varchar(100) NOT NULL,
  `arrival_airport` varchar(100) NOT NULL,
  `departure_time` varchar(50) NOT NULL,
  `arrival_time` varchar(50) NOT NULL,
  `price` double NOT NULL,
  `available_seats` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flight`
--

INSERT INTO `flight` (`flight_id`, `flight_number`, `airline_name`, `departure_airport`, `arrival_airport`, `departure_time`, `arrival_time`, `price`, `available_seats`) VALUES
(1, 'AI-101', 'Air India', 'New Delhi (DEL)', 'Mumbai (BOM)', '2026-08-10 08:00', '2026-08-10 10:15', 5500, 158),
(2, '6E-202', 'IndiGo', 'Mumbai (BOM)', 'Bengaluru (BLR)', '2026-08-10 11:30', '2026-08-10 13:15', 4200, 138),
(3, 'UK-303', 'Vistara', 'Bengaluru (BLR)', 'New Delhi (DEL)', '2026-08-11 15:00', '2026-08-11 17:45', 6800, 113),
(4, 'SG-404', 'SpiceJet', 'New Delhi (DEL)', 'Kolkata (CCU)', '2026-08-11 09:15', '2026-08-11 11:30', 3800, 135),
(5, 'AI-505', 'Air India', 'Mumbai (BOM)', 'Chennai (MAA)', '2026-08-12 06:45', '2026-08-12 08:45', 4900, 149);

--
-- Triggers `flight`
--
DELIMITER $$
CREATE TRIGGER `trg_flight_before_insert` BEFORE INSERT ON `flight` FOR EACH ROW BEGIN
    IF NEW.departure_time >= NEW.arrival_time THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Flight Time';
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_flight_before_update` BEFORE UPDATE ON `flight` FOR EACH ROW BEGIN
    IF NEW.departure_time >= NEW.arrival_time THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Flight Time';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `hotel`
--

CREATE TABLE `hotel` (
  `hotel_id` int(11) NOT NULL,
  `hotel_name` varchar(100) NOT NULL,
  `city` varchar(50) NOT NULL,
  `price_per_night` double NOT NULL,
  `available_rooms` int(11) NOT NULL,
  `rating` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hotel`
--

INSERT INTO `hotel` (`hotel_id`, `hotel_name`, `city`, `price_per_night`, `available_rooms`, `rating`) VALUES
(1, 'Taj Mahal Palace', 'Mumbai', 12000, 25, 4.9),
(2, 'The Leela Palace', 'New Delhi', 15000, 20, 4.8),
(3, 'The Oberoi', 'Bengaluru', 11000, 30, 4.7);

--
-- Triggers `hotel`
--
DELIMITER $$
CREATE TRIGGER `trg_hotel_before_insert` BEFORE INSERT ON `hotel` FOR EACH ROW BEGIN
    IF EXISTS (SELECT 1 FROM hotel WHERE hotel_name = NEW.hotel_name AND city = NEW.city) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Hotel in City';
    END IF;
    IF NEW.price_per_night < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Negative Hotel Price';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `hotel_booking`
--

CREATE TABLE `hotel_booking` (
  `hotel_booking_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `hotel_id` int(11) NOT NULL,
  `check_in_date` varchar(50) NOT NULL,
  `check_out_date` varchar(50) NOT NULL,
  `num_rooms` int(11) NOT NULL,
  `total_price` double NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `hotel_booking`
--
DELIMITER $$
CREATE TRIGGER `trg_hotel_booking_before_insert` BEFORE INSERT ON `hotel_booking` FOR EACH ROW BEGIN
    IF NEW.check_out_date <= NEW.check_in_date THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Check-out must be after Check-in';
    END IF;
    IF NEW.check_in_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Check-in cannot be in the past';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE `notification` (
  `notification_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `created_at` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notification`
--

INSERT INTO `notification` (`notification_id`, `customer_id`, `message`, `created_at`, `status`) VALUES
(1, 1, 'Flight AI-101 booking confirmed. Ticket: TKT-10001', '2026-08-01 10:00:00', 'UNREAD'),
(2, 2, 'Flight 6E-202 booking confirmed. Ticket: TKT-10003', '2026-08-01 11:30:00', 'UNREAD');

--
-- Triggers `notification`
--
DELIMITER $$
CREATE TRIGGER `trg_notification_before_insert` BEFORE INSERT ON `notification` FOR EACH ROW BEGIN
    IF NEW.created_at IS NULL THEN
        SET NEW.created_at = CURRENT_TIMESTAMP;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `passenger`
--

CREATE TABLE `passenger` (
  `passenger_id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `age` int(11) NOT NULL,
  `gender` varchar(10) NOT NULL,
  `seat_number` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `passenger`
--

INSERT INTO `passenger` (`passenger_id`, `booking_id`, `name`, `age`, `gender`, `seat_number`) VALUES
(1, 1, 'Rohan Raj', 28, 'Male', '12A'),
(2, 2, 'Rohan Raj', 28, 'Male', '14C'),
(3, 3, 'Priya Sharma', 26, 'Female', '08F'),
(4, 4, 'Aarav Gupta', 32, 'Male', '05B'),
(5, 5, 'Ananya Verma', 24, 'Female', '03A'),
(6, 6, 'Vikram Singh', 35, 'Male', '11D'),
(7, 7, 'Vikram Singh', 35, 'Male', '18F');

--
-- Triggers `passenger`
--
DELIMITER $$
CREATE TRIGGER `trg_passenger_before_insert` BEFORE INSERT ON `passenger` FOR EACH ROW BEGIN
    IF NEW.name IS NULL OR TRIM(NEW.name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty Passenger Name';
    END IF;
    IF NEW.age < 0 OR NEW.age > 120 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Passenger Age';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `payment_date` varchar(50) NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`payment_id`, `booking_id`, `amount`, `payment_date`, `payment_method`, `status`) VALUES
(1, 1, 5500, '2026-08-01', 'UPI', 'COMPLETED'),
(2, 2, 6800, '2026-08-02', 'Credit Card', 'COMPLETED'),
(3, 3, 4200, '2026-08-01', 'Debit Card', 'COMPLETED'),
(4, 4, 5500, '2026-08-01', 'UPI', 'COMPLETED'),
(5, 5, 6800, '2026-08-02', 'UPI', 'COMPLETED'),
(6, 6, 4200, '2026-08-01', 'Credit Card', 'COMPLETED'),
(7, 7, 4900, '2026-08-03', 'NetBanking', 'COMPLETED');

--
-- Triggers `payment`
--
DELIMITER $$
CREATE TRIGGER `trg_payment_before_insert` BEFORE INSERT ON `payment` FOR EACH ROW BEGIN
    IF NEW.amount <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Payment Amount';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM booking WHERE booking_id = NEW.booking_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Booking';
    END IF;
    IF EXISTS (SELECT 1 FROM payment WHERE booking_id = NEW.booking_id AND status = 'COMPLETED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Payment for Booking';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `taxi`
--

CREATE TABLE `taxi` (
  `taxi_id` int(11) NOT NULL,
  `driver_name` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `vehicle_number` varchar(20) NOT NULL,
  `vehicle_type` varchar(50) NOT NULL,
  `price_per_km` double NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `taxi`
--

INSERT INTO `taxi` (`taxi_id`, `driver_name`, `phone_number`, `vehicle_number`, `vehicle_type`, `price_per_km`, `status`) VALUES
(1, 'Ramesh Kumar', '+91-9811122233', 'DL-01-AB-1234', 'Sedan', 15, 'AVAILABLE'),
(2, 'Suresh Yadav', '+91-9822233344', 'MH-02-CD-5678', 'SUV', 22, 'AVAILABLE');

--
-- Triggers `taxi`
--
DELIMITER $$
CREATE TRIGGER `trg_taxi_before_insert` BEFORE INSERT ON `taxi` FOR EACH ROW BEGIN
    IF NEW.price_per_km < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Negative Taxi Fare';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `ticket`
--

CREATE TABLE `ticket` (
  `ticket_id` int(11) NOT NULL,
  `booking_id` int(11) NOT NULL,
  `passenger_id` int(11) NOT NULL,
  `ticket_number` varchar(50) NOT NULL,
  `seat_number` varchar(10) NOT NULL,
  `price` double NOT NULL,
  `issue_date` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ticket`
--

INSERT INTO `ticket` (`ticket_id`, `booking_id`, `passenger_id`, `ticket_number`, `seat_number`, `price`, `issue_date`) VALUES
(1, 1, 1, 'TKT-10001', '12A', 5500, '2026-08-01'),
(2, 2, 2, 'TKT-10002', '14C', 6800, '2026-08-02'),
(3, 3, 3, 'TKT-10003', '08F', 4200, '2026-08-01'),
(4, 4, 4, 'TKT-10004', '05B', 5500, '2026-08-01'),
(5, 5, 5, 'TKT-10005', '03A', 6800, '2026-08-02'),
(6, 6, 6, 'TKT-10006', '11D', 4200, '2026-08-01'),
(7, 7, 7, 'TKT-10007', '18F', 4900, '2026-08-03');

--
-- Triggers `ticket`
--
DELIMITER $$
CREATE TRIGGER `trg_ticket_before_insert` BEFORE INSERT ON `ticket` FOR EACH ROW BEGIN
    IF NEW.ticket_number IS NULL OR TRIM(NEW.ticket_number) = '' THEN
        SET NEW.ticket_number = CONCAT('TKT-', UNIX_TIMESTAMP(), '-', LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END IF;
    IF NEW.issue_date IS NULL THEN
        SET NEW.issue_date = CURDATE();
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_booking_details`
-- (See below for the actual view)
--
CREATE TABLE `vw_booking_details` (
`booking_id` int(11)
,`customer_id` int(11)
,`customer_name` varchar(100)
,`customer_email` varchar(100)
,`customer_phone` varchar(20)
,`flight_id` int(11)
,`flight_number` varchar(20)
,`airline_name` varchar(100)
,`departure_airport` varchar(100)
,`arrival_airport` varchar(100)
,`departure_time` varchar(50)
,`arrival_time` varchar(50)
,`booking_date` varchar(50)
,`booking_status` varchar(20)
,`total_amount` double
,`ticket_number` varchar(50)
,`seat_number` varchar(10)
,`payment_method` varchar(50)
,`payment_status` varchar(20)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_customer_travel_summary`
-- (See below for the actual view)
--
CREATE TABLE `vw_customer_travel_summary` (
`customer_id` int(11)
,`customer_name` varchar(100)
,`email` varchar(100)
,`phone` varchar(20)
,`passport_number` varchar(50)
,`total_flight_bookings` bigint(21)
,`total_flight_spent` double
,`total_hotel_bookings` bigint(21)
,`total_hotel_spent` double
,`grand_total_spent` double
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_flight_occupancy`
-- (See below for the actual view)
--
CREATE TABLE `vw_flight_occupancy` (
`flight_id` int(11)
,`flight_number` varchar(20)
,`airline_name` varchar(100)
,`departure_airport` varchar(100)
,`arrival_airport` varchar(100)
,`price` double
,`available_seats` int(11)
,`total_bookings` bigint(21)
,`total_revenue` double
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_hotel_booking_details`
-- (See below for the actual view)
--
CREATE TABLE `vw_hotel_booking_details` (
`hotel_booking_id` int(11)
,`customer_id` int(11)
,`customer_name` varchar(100)
,`hotel_id` int(11)
,`hotel_name` varchar(100)
,`city` varchar(50)
,`check_in_date` varchar(50)
,`check_out_date` varchar(50)
,`num_rooms` int(11)
,`total_price` double
,`booking_status` varchar(20)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `vw_unread_notifications`
-- (See below for the actual view)
--
CREATE TABLE `vw_unread_notifications` (
`notification_id` int(11)
,`customer_id` int(11)
,`customer_name` varchar(100)
,`message` text
,`created_at` varchar(50)
);

-- --------------------------------------------------------

--
-- Structure for view `vw_booking_details`
--
DROP TABLE IF EXISTS `vw_booking_details`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_booking_details`  AS SELECT `b`.`booking_id` AS `booking_id`, `c`.`customer_id` AS `customer_id`, `c`.`name` AS `customer_name`, `c`.`email` AS `customer_email`, `c`.`phone` AS `customer_phone`, `f`.`flight_id` AS `flight_id`, `f`.`flight_number` AS `flight_number`, `f`.`airline_name` AS `airline_name`, `f`.`departure_airport` AS `departure_airport`, `f`.`arrival_airport` AS `arrival_airport`, `f`.`departure_time` AS `departure_time`, `f`.`arrival_time` AS `arrival_time`, `b`.`booking_date` AS `booking_date`, `b`.`status` AS `booking_status`, `b`.`total_amount` AS `total_amount`, `t`.`ticket_number` AS `ticket_number`, `t`.`seat_number` AS `seat_number`, `p`.`payment_method` AS `payment_method`, `p`.`status` AS `payment_status` FROM ((((`booking` `b` join `customer` `c` on(`b`.`customer_id` = `c`.`customer_id`)) join `flight` `f` on(`b`.`flight_id` = `f`.`flight_id`)) left join `ticket` `t` on(`b`.`booking_id` = `t`.`booking_id`)) left join `payment` `p` on(`b`.`booking_id` = `p`.`booking_id`)) ;

-- --------------------------------------------------------

--
-- Structure for view `vw_customer_travel_summary`
--
DROP TABLE IF EXISTS `vw_customer_travel_summary`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_customer_travel_summary`  AS SELECT `c`.`customer_id` AS `customer_id`, `c`.`name` AS `customer_name`, `c`.`email` AS `email`, `c`.`phone` AS `phone`, `c`.`passport_number` AS `passport_number`, count(distinct `b`.`booking_id`) AS `total_flight_bookings`, coalesce(sum(`b`.`total_amount`),0) AS `total_flight_spent`, count(distinct `hb`.`hotel_booking_id`) AS `total_hotel_bookings`, coalesce(sum(`hb`.`total_price`),0) AS `total_hotel_spent`, coalesce(sum(`b`.`total_amount`),0) + coalesce(sum(`hb`.`total_price`),0) AS `grand_total_spent` FROM ((`customer` `c` left join `booking` `b` on(`c`.`customer_id` = `b`.`customer_id` and `b`.`status` = 'CONFIRMED')) left join `hotel_booking` `hb` on(`c`.`customer_id` = `hb`.`customer_id` and `hb`.`status` = 'CONFIRMED')) GROUP BY `c`.`customer_id`, `c`.`name`, `c`.`email`, `c`.`phone`, `c`.`passport_number` ;

-- --------------------------------------------------------

--
-- Structure for view `vw_flight_occupancy`
--
DROP TABLE IF EXISTS `vw_flight_occupancy`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_flight_occupancy`  AS SELECT `f`.`flight_id` AS `flight_id`, `f`.`flight_number` AS `flight_number`, `f`.`airline_name` AS `airline_name`, `f`.`departure_airport` AS `departure_airport`, `f`.`arrival_airport` AS `arrival_airport`, `f`.`price` AS `price`, `f`.`available_seats` AS `available_seats`, count(`b`.`booking_id`) AS `total_bookings`, coalesce(sum(`b`.`total_amount`),0) AS `total_revenue` FROM (`flight` `f` left join `booking` `b` on(`f`.`flight_id` = `b`.`flight_id` and `b`.`status` = 'CONFIRMED')) GROUP BY `f`.`flight_id`, `f`.`flight_number`, `f`.`airline_name`, `f`.`departure_airport`, `f`.`arrival_airport`, `f`.`price`, `f`.`available_seats` ;

-- --------------------------------------------------------

--
-- Structure for view `vw_hotel_booking_details`
--
DROP TABLE IF EXISTS `vw_hotel_booking_details`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_hotel_booking_details`  AS SELECT `hb`.`hotel_booking_id` AS `hotel_booking_id`, `c`.`customer_id` AS `customer_id`, `c`.`name` AS `customer_name`, `h`.`hotel_id` AS `hotel_id`, `h`.`hotel_name` AS `hotel_name`, `h`.`city` AS `city`, `hb`.`check_in_date` AS `check_in_date`, `hb`.`check_out_date` AS `check_out_date`, `hb`.`num_rooms` AS `num_rooms`, `hb`.`total_price` AS `total_price`, `hb`.`status` AS `booking_status` FROM ((`hotel_booking` `hb` join `customer` `c` on(`hb`.`customer_id` = `c`.`customer_id`)) join `hotel` `h` on(`hb`.`hotel_id` = `h`.`hotel_id`)) ;

-- --------------------------------------------------------

--
-- Structure for view `vw_unread_notifications`
--
DROP TABLE IF EXISTS `vw_unread_notifications`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `vw_unread_notifications`  AS SELECT `n`.`notification_id` AS `notification_id`, `c`.`customer_id` AS `customer_id`, `c`.`name` AS `customer_name`, `n`.`message` AS `message`, `n`.`created_at` AS `created_at` FROM (`notification` `n` join `customer` `c` on(`n`.`customer_id` = `c`.`customer_id`)) WHERE `n`.`status` = 'UNREAD' ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `airport`
--
ALTER TABLE `airport`
  ADD PRIMARY KEY (`airport_id`),
  ADD UNIQUE KEY `airport_code` (`airport_code`);

--
-- Indexes for table `booking`
--
ALTER TABLE `booking`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `flight_id` (`flight_id`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `feedback`
--
ALTER TABLE `feedback`
  ADD PRIMARY KEY (`feedback_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `flight`
--
ALTER TABLE `flight`
  ADD PRIMARY KEY (`flight_id`),
  ADD UNIQUE KEY `flight_number` (`flight_number`);

--
-- Indexes for table `hotel`
--
ALTER TABLE `hotel`
  ADD PRIMARY KEY (`hotel_id`);

--
-- Indexes for table `hotel_booking`
--
ALTER TABLE `hotel_booking`
  ADD PRIMARY KEY (`hotel_booking_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `hotel_id` (`hotel_id`);

--
-- Indexes for table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `passenger`
--
ALTER TABLE `passenger`
  ADD PRIMARY KEY (`passenger_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Indexes for table `taxi`
--
ALTER TABLE `taxi`
  ADD PRIMARY KEY (`taxi_id`),
  ADD UNIQUE KEY `vehicle_number` (`vehicle_number`);

--
-- Indexes for table `ticket`
--
ALTER TABLE `ticket`
  ADD PRIMARY KEY (`ticket_id`),
  ADD UNIQUE KEY `ticket_number` (`ticket_number`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `passenger_id` (`passenger_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `airport`
--
ALTER TABLE `airport`
  MODIFY `airport_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `booking`
--
ALTER TABLE `booking`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `feedback`
--
ALTER TABLE `feedback`
  MODIFY `feedback_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `flight`
--
ALTER TABLE `flight`
  MODIFY `flight_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `hotel`
--
ALTER TABLE `hotel`
  MODIFY `hotel_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `hotel_booking`
--
ALTER TABLE `hotel_booking`
  MODIFY `hotel_booking_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notification`
--
ALTER TABLE `notification`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `passenger`
--
ALTER TABLE `passenger`
  MODIFY `passenger_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `taxi`
--
ALTER TABLE `taxi`
  MODIFY `taxi_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `ticket`
--
ALTER TABLE `ticket`
  MODIFY `ticket_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `booking`
--
ALTER TABLE `booking`
  ADD CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`flight_id`) REFERENCES `flight` (`flight_id`) ON DELETE CASCADE;

--
-- Constraints for table `feedback`
--
ALTER TABLE `feedback`
  ADD CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE;

--
-- Constraints for table `hotel_booking`
--
ALTER TABLE `hotel_booking`
  ADD CONSTRAINT `hotel_booking_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `hotel_booking_ibfk_2` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`hotel_id`) ON DELETE CASCADE;

--
-- Constraints for table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE;

--
-- Constraints for table `passenger`
--
ALTER TABLE `passenger`
  ADD CONSTRAINT `passenger_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`) ON DELETE CASCADE;

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`) ON DELETE CASCADE;

--
-- Constraints for table `ticket`
--
ALTER TABLE `ticket`
  ADD CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `ticket_ibfk_2` FOREIGN KEY (`passenger_id`) REFERENCES `passenger` (`passenger_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
