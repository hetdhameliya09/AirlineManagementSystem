USE airline;

DELIMITER $$

-- 1. Stored Procedure: Add Customer
DROP PROCEDURE IF EXISTS sp_add_customer$$
CREATE PROCEDURE sp_add_customer(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_name VARCHAR(100),
    IN p_email VARCHAR(100),
    IN p_phone VARCHAR(20),
    IN p_passport VARCHAR(50),
    OUT p_customer_id INT
)
BEGIN
    INSERT INTO customer (username, password, name, email, phone, passport_number)
    VALUES (p_username, p_password, p_name, p_email, p_phone, p_passport);
    
    SET p_customer_id = LAST_INSERT_ID();
END$$

-- 2. Stored Procedure: Search Flights by Route
DROP PROCEDURE IF EXISTS sp_search_flights$$
CREATE PROCEDURE sp_search_flights(
    IN p_departure VARCHAR(100),
    IN p_arrival VARCHAR(100)
)
BEGIN
    SELECT * 
    FROM flight 
    WHERE departure_airport LIKE CONCAT('%', p_departure, '%')
      AND arrival_airport LIKE CONCAT('%', p_arrival, '%');
END$$

-- 3. Stored Procedure: Book Flight (Atomic Transaction)
DROP PROCEDURE IF EXISTS sp_book_flight$$
CREATE PROCEDURE sp_book_flight(
    IN p_customer_id INT,
    IN p_flight_id INT,
    IN p_booking_date DATE,
    IN p_passenger_name VARCHAR(100),
    IN p_passenger_age INT,
    IN p_passenger_gender VARCHAR(10),
    IN p_seat_number VARCHAR(10),
    OUT p_booking_id INT,
    OUT p_passenger_id INT,
    OUT p_ticket_number VARCHAR(50),
    OUT p_result_code INT
)
BEGIN
    DECLARE v_seats INT;
    DECLARE v_price DOUBLE;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1; -- Error / Rollback
    END;

    START TRANSACTION;

    -- Check seat availability and price
    SELECT available_seats, price INTO v_seats, v_price 
    FROM flight 
    WHERE flight_id = p_flight_id FOR UPDATE;

    IF v_seats IS NULL OR v_seats <= 0 THEN
        SET p_result_code = 0; -- No seats available
        ROLLBACK;
    ELSE
        -- Insert Booking
        INSERT INTO booking (customer_id, flight_id, booking_date, status, total_amount)
        VALUES (p_customer_id, p_flight_id, p_booking_date, 'CONFIRMED', v_price);
        SET p_booking_id = LAST_INSERT_ID();

        -- Insert Passenger
        INSERT INTO passenger (booking_id, name, age, gender, seat_number)
        VALUES (p_booking_id, p_passenger_name, p_passenger_age, p_passenger_gender, p_seat_number);
        SET p_passenger_id = LAST_INSERT_ID();

        -- Generate Ticket
        SET p_ticket_number = CONCAT('TKT-', UNIX_TIMESTAMP() % 100000, '-', LPAD(FLOOR(RAND() * 100), 2, '0'));
        INSERT INTO ticket (booking_id, passenger_id, ticket_number, seat_number, price, issue_date)
        VALUES (p_booking_id, p_passenger_id, p_ticket_number, p_seat_number, v_price, p_booking_date);

        -- Add Notification
        INSERT INTO notification (customer_id, message, created_at, status)
        VALUES (p_customer_id, CONCAT('Booking confirmed via procedure! Booking ID: ', p_booking_id, ', Ticket: ', p_ticket_number), NOW(), 'UNREAD');

        SET p_result_code = 1; -- Success
        COMMIT;
    END IF;
END$$

-- 4. Stored Procedure: Cancel Booking (Atomic Transaction)
DROP PROCEDURE IF EXISTS sp_cancel_booking$$
CREATE PROCEDURE sp_cancel_booking(
    IN p_booking_id INT,
    IN p_customer_id INT,
    OUT p_result_code INT
)
BEGIN
    DECLARE v_flight_id INT;
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_owner_id INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1;
    END;

    START TRANSACTION;

    SELECT flight_id, status, customer_id INTO v_flight_id, v_current_status, v_owner_id
    FROM booking 
    WHERE booking_id = p_booking_id FOR UPDATE;

    IF v_owner_id IS NULL OR v_owner_id <> p_customer_id THEN
        SET p_result_code = 0; -- Unauthorized / Not found
        ROLLBACK;
    ELSEIF UPPER(v_current_status) = 'CANCELLED' THEN
        SET p_result_code = 2; -- Already cancelled
        ROLLBACK;
    ELSE
        -- Update booking status
        UPDATE booking SET status = 'CANCELLED' WHERE booking_id = p_booking_id;

        -- Restore flight seat
        UPDATE flight SET available_seats = available_seats + 1 WHERE flight_id = v_flight_id;

        -- Refund existing completed payments
        UPDATE payment SET status = 'REFUNDED' WHERE booking_id = p_booking_id AND status = 'COMPLETED';

        -- Send Notification
        INSERT INTO notification (customer_id, message, created_at, status)
        VALUES (p_customer_id, CONCAT('Booking ID ', p_booking_id, ' has been CANCELLED via procedure.'), NOW(), 'UNREAD');

        SET p_result_code = 1; -- Success
        COMMIT;
    END IF;
END$$

-- 5. Stored Procedure: Process Payment
DROP PROCEDURE IF EXISTS sp_process_payment$$
CREATE PROCEDURE sp_process_payment(
    IN p_booking_id INT,
    IN p_amount DOUBLE,
    IN p_payment_date DATE,
    IN p_payment_method VARCHAR(50),
    OUT p_result_code INT
)
BEGIN
    DECLARE v_status VARCHAR(20);
    DECLARE v_existing_payment INT;

    SELECT status INTO v_status FROM booking WHERE booking_id = p_booking_id;

    IF v_status IS NULL THEN
        SET p_result_code = 0; -- Booking not found
    ELSEIF UPPER(v_status) = 'CANCELLED' THEN
        SET p_result_code = 2; -- Booking cancelled
    ELSE
        SELECT COUNT(*) INTO v_existing_payment 
        FROM payment 
        WHERE booking_id = p_booking_id AND status = 'COMPLETED';

        IF v_existing_payment > 0 THEN
            SET p_result_code = 3; -- Already paid
        ELSE
            INSERT INTO payment (booking_id, amount, payment_date, payment_method, status)
            VALUES (p_booking_id, p_amount, p_payment_date, p_payment_method, 'COMPLETED');
            SET p_result_code = 1; -- Success
        END IF;
    END IF;
END$$

DELIMITER ;
