USE airline;

-- 1. View: Comprehensive Customer Booking Details
CREATE OR REPLACE VIEW vw_customer_booking_details AS
SELECT 
    b.booking_id,
    b.customer_id,
    c.name AS customer_name,
    c.email AS customer_email,
    c.phone AS customer_phone,
    b.flight_id,
    f.flight_number,
    f.airline_name,
    f.departure_airport,
    f.arrival_airport,
    f.departure_time,
    f.arrival_time,
    p.passenger_id,
    p.name AS passenger_name,
    p.age AS passenger_age,
    p.gender AS passenger_gender,
    p.seat_number,
    t.ticket_id,
    t.ticket_number,
    b.booking_date,
    b.status AS booking_status,
    b.total_amount,
    pay.payment_id,
    pay.payment_method,
    pay.status AS payment_status
FROM booking b
JOIN customer c ON b.customer_id = c.customer_id
JOIN flight f ON b.flight_id = f.flight_id
LEFT JOIN passenger p ON b.booking_id = p.booking_id
LEFT JOIN ticket t ON b.booking_id = t.booking_id AND p.passenger_id = t.passenger_id
LEFT JOIN payment pay ON b.booking_id = pay.booking_id;

-- 2. View: Flight Schedule and Capacity Summary
CREATE OR REPLACE VIEW vw_flight_schedule_summary AS
SELECT 
    flight_id,
    flight_number,
    airline_name,
    departure_airport,
    arrival_airport,
    departure_time,
    arrival_time,
    price,
    available_seats,
    CASE 
        WHEN available_seats > 50 THEN 'High Availability'
        WHEN available_seats > 0 THEN 'Limited Seats'
        ELSE 'Sold Out'
    END AS availability_status
FROM flight;

-- 3. View: Customer Spending & Booking History Summary
CREATE OR REPLACE VIEW vw_customer_flight_history AS
SELECT 
    c.customer_id,
    c.username,
    c.name AS customer_name,
    c.email,
    c.phone,
    COUNT(b.booking_id) AS total_bookings,
    SUM(CASE WHEN UPPER(b.status) = 'CONFIRMED' THEN 1 ELSE 0 END) AS active_bookings,
    SUM(CASE WHEN UPPER(b.status) = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bookings,
    COALESCE(SUM(CASE WHEN UPPER(b.status) = 'CONFIRMED' THEN b.total_amount ELSE 0 END), 0) AS total_spent
FROM customer c
LEFT JOIN booking b ON c.customer_id = b.customer_id
GROUP BY c.customer_id, c.username, c.name, c.email, c.phone;

-- 4. View: Hotel Booking Details Summary
CREATE OR REPLACE VIEW vw_hotel_booking_details AS
SELECT 
    hb.hotel_booking_id,
    hb.customer_id,
    c.name AS customer_name,
    c.phone AS customer_phone,
    hb.hotel_id,
    h.hotel_name,
    h.city AS hotel_city,
    h.rating AS hotel_rating,
    hb.check_in_date,
    hb.check_out_date,
    hb.num_rooms,
    hb.total_price,
    hb.status AS booking_status
FROM hotel_booking hb
JOIN customer c ON hb.customer_id = c.customer_id
JOIN hotel h ON hb.hotel_id = h.hotel_id;

-- 5. View: Customer Feedback Summary
CREATE OR REPLACE VIEW vw_feedback_summary AS
SELECT 
    fb.feedback_id,
    fb.customer_id,
    c.name AS customer_name,
    c.email AS customer_email,
    fb.rating,
    fb.comments,
    fb.created_at
FROM feedback fb
JOIN customer c ON fb.customer_id = c.customer_id;
