USE airline;

DELIMITER $$

DROP TRIGGER IF EXISTS trg_booking_before_insert$$
CREATE TRIGGER trg_booking_before_insert
BEFORE INSERT ON booking
FOR EACH ROW
BEGIN
    DECLARE seats INT;
    SELECT available_seats INTO seats FROM flight WHERE flight_id = NEW.flight_id;
    IF seats IS NULL OR seats <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No Available Seats';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_booking_after_insert$$
CREATE TRIGGER trg_booking_after_insert
AFTER INSERT ON booking
FOR EACH ROW
BEGIN
    UPDATE flight 
    SET available_seats = available_seats - 1 
    WHERE flight_id = NEW.flight_id;
END$$

DROP TRIGGER IF EXISTS trg_booking_after_delete$$
CREATE TRIGGER trg_booking_after_delete
AFTER DELETE ON booking
FOR EACH ROW
BEGIN
    UPDATE flight 
    SET available_seats = available_seats + 1 
    WHERE flight_id = OLD.flight_id;
END$$

DROP TRIGGER IF EXISTS trg_flight_before_insert$$
CREATE TRIGGER trg_flight_before_insert
BEFORE INSERT ON flight
FOR EACH ROW
BEGIN
    IF NEW.departure_time >= NEW.arrival_time THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Flight Time';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_flight_before_update$$
CREATE TRIGGER trg_flight_before_update
BEFORE UPDATE ON flight
FOR EACH ROW
BEGIN
    IF NEW.departure_time >= NEW.arrival_time THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Flight Time';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_passenger_before_insert$$
CREATE TRIGGER trg_passenger_before_insert
BEFORE INSERT ON passenger
FOR EACH ROW
BEGIN
    IF NEW.name IS NULL OR TRIM(NEW.name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Empty Passenger Name';
    END IF;
    IF NEW.age < 0 OR NEW.age > 120 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Passenger Age';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_customer_before_insert$$
CREATE TRIGGER trg_customer_before_insert
BEFORE INSERT ON customer
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM customer WHERE email = NEW.email) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Email';
    END IF;
    IF EXISTS (SELECT 1 FROM customer WHERE username = NEW.username) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Username';
    END IF;
    IF NEW.name IS NULL OR TRIM(NEW.name) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blank Customer Name';
    END IF;
    IF NEW.phone NOT REGEXP '^[0-9]{10}$' AND NEW.phone NOT REGEXP '^\\+?[0-9]{10,15}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Phone Number';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_payment_before_insert$$
CREATE TRIGGER trg_payment_before_insert
BEFORE INSERT ON payment
FOR EACH ROW
BEGIN
    IF NEW.amount <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Payment Amount';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM booking WHERE booking_id = NEW.booking_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Booking';
    END IF;
    IF EXISTS (SELECT 1 FROM payment WHERE booking_id = NEW.booking_id AND status = 'COMPLETED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Payment for Booking';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_hotel_booking_before_insert$$
CREATE TRIGGER trg_hotel_booking_before_insert
BEFORE INSERT ON hotel_booking
FOR EACH ROW
BEGIN
    IF NEW.check_out_date <= NEW.check_in_date THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Check-out must be after Check-in';
    END IF;
    IF NEW.check_in_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Check-in cannot be in the past';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_taxi_before_insert$$
CREATE TRIGGER trg_taxi_before_insert
BEFORE INSERT ON taxi
FOR EACH ROW
BEGIN
    IF NEW.price_per_km < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Negative Taxi Fare';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_ticket_before_insert$$
CREATE TRIGGER trg_ticket_before_insert
BEFORE INSERT ON ticket
FOR EACH ROW
BEGIN
    IF NEW.ticket_number IS NULL OR TRIM(NEW.ticket_number) = '' THEN
        SET NEW.ticket_number = CONCAT('TKT-', UNIX_TIMESTAMP(), '-', LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END IF;
    IF NEW.issue_date IS NULL THEN
        SET NEW.issue_date = CURDATE();
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_notification_before_insert$$
CREATE TRIGGER trg_notification_before_insert
BEFORE INSERT ON notification
FOR EACH ROW
BEGIN
    IF NEW.created_at IS NULL THEN
        SET NEW.created_at = CURRENT_TIMESTAMP;
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_feedback_before_insert$$
CREATE TRIGGER trg_feedback_before_insert
BEFORE INSERT ON feedback
FOR EACH ROW
BEGIN
    IF NEW.rating < 1 OR NEW.rating > 5 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Rating';
    END IF;
    IF NEW.comments IS NULL OR TRIM(NEW.comments) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blank Feedback Comments';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_airport_before_insert$$
CREATE TRIGGER trg_airport_before_insert
BEFORE INSERT ON airport
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM airport WHERE airport_code = NEW.airport_code) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Airport Code';
    END IF;
    IF NEW.airport_code NOT REGEXP '^[A-Z]{3}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Airport Code';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_hotel_before_insert$$
CREATE TRIGGER trg_hotel_before_insert
BEFORE INSERT ON hotel
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM hotel WHERE hotel_name = NEW.hotel_name AND city = NEW.city) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Hotel in City';
    END IF;
    IF NEW.price_per_night < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Negative Hotel Price';
    END IF;
END$$

DROP TRIGGER IF EXISTS trg_admin_before_insert$$
CREATE TRIGGER trg_admin_before_insert
BEFORE INSERT ON admin
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM admin WHERE username = NEW.username) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate Admin Username';
    END IF;
    IF NEW.password IS NULL OR TRIM(NEW.password) = '' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blank Password';
    END IF;
END$$

DELIMITER ;
