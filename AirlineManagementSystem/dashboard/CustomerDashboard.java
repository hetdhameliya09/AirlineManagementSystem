package dashboard;

import dao.BookingDAO;
import dao.CustomerDAO;
import dao.FeedbackDAO;
import dao.FlightDAO;
import dao.HotelBookingDAO;
import dao.HotelDAO;
import dao.NotificationDAO;
import dao.PassengerDAO;
import dao.PaymentDAO;
import dao.TaxiDAO;
import dao.TicketDAO;
import model.Booking;
import model.Customer;
import model.Feedback;
import model.Flight;
import model.Hotel;
import model.HotelBooking;
import model.Notification;
import model.Passenger;
import model.Payment;
import model.Taxi;
import model.Ticket;
import validation.DateValidator;
import validation.ValidationUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class CustomerDashboard {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final FlightDAO flightDAO = new FlightDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final HotelDAO hotelDAO = new HotelDAO();
    private final HotelBookingDAO hotelBookingDAO = new HotelBookingDAO();
    private final TaxiDAO taxiDAO = new TaxiDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final Scanner sc = new Scanner(System.in);

    // Admin view for Manage Customers
    public void adminMenu() {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("          CUSTOMER MANAGEMENT");
            System.out.println("======================================");
            System.out.println("1. Add Customer");
            System.out.println("2. View All Customers");
            System.out.println("3. Search Customer");
            System.out.println("4. Update Customer");
            System.out.println("5. Delete Customer");
            System.out.println("6. Back");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    viewAllCustomers();
                    break;
                case 3:
                    searchCustomer();
                    break;
                case 4:
                    updateCustomer();
                    break;
                case 5:
                    deleteCustomer();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    // Customer Portal Menu for Logged-In Customer
    public void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\n======================================");
            System.out.println("      CUSTOMER PORTAL - " + customer.getName().toUpperCase());
            System.out.println("======================================");
            System.out.println("1. Search Flights");
            System.out.println("2. Book Flight");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. View Ticket");
            System.out.println("6. Process Payment");
            System.out.println("7. Book Hotel");
            System.out.println("8. Book Taxi");
            System.out.println("9. View Notifications");
            System.out.println("10. Give Feedback");
            System.out.println("11. Logout");
            System.out.print("Enter Choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    searchFlights();
                    break;
                case 2:
                    bookFlight(customer);
                    break;
                case 3:
                    viewMyBookings(customer);
                    break;
                case 4:
                    cancelBooking(customer);
                    break;
                case 5:
                    viewTicket(customer);
                    break;
                case 6:
                    processPayment(customer);
                    break;
                case 7:
                    bookHotel(customer);
                    break;
                case 8:
                    bookTaxi(customer);
                    break;
                case 9:
                    viewNotifications(customer);
                    break;
                case 10:
                    giveFeedback(customer);
                    break;
                case 11:
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid Choice. Please try again.");
            }
        }
    }

    // Admin CRUD Operations
    private void addCustomer() {
        System.out.println("\n--- Add New Customer ---");
        String username = readNonEmptyString("Enter Username: ");
        String password = readNonEmptyString("Enter Password: ");
        String name = readNonEmptyString("Enter Full Name: ");
        
        String email;
        do {
            email = readNonEmptyString("Enter Email: ");
            if (!ValidationUtils.isValidEmail(email)) {
                System.out.println("Invalid email format. Please re-enter.");
            }
        } while (!ValidationUtils.isValidEmail(email));

        String phone;
        do {
            phone = readNonEmptyString("Enter Phone: ");
            if (!ValidationUtils.isValidPhone(phone)) {
                System.out.println("Invalid phone format. Please re-enter.");
            }
        } while (!ValidationUtils.isValidPhone(phone));

        String passport = readNonEmptyString("Enter Passport Number: ");

        Customer c = new Customer(username, password, name, email, phone, passport);
        if (customerDAO.addCustomer(c)) {
            System.out.println("SUCCESS: Customer added successfully!");
        } else {
            System.out.println("ERROR: Failed to add customer (Username might already exist).");
        }
    }

    private void viewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        List<Customer> list = customerDAO.getAllCustomers();
        if (list.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            for (Customer c : list) {
                System.out.println(c);
            }
        }
    }

    private void searchCustomer() {
        System.out.println("\n--- Search Customer ---");
        System.out.print("Enter Customer ID: ");
        int id = readInt();
        Customer c = customerDAO.searchCustomerById(id);
        if (c == null) {
            System.out.println("No customer found with ID: " + id);
        } else {
            System.out.println(c);
        }
    }

    private void updateCustomer() {
        System.out.println("\n--- Update Customer ---");
        System.out.print("Enter Customer ID to Update: ");
        int id = readInt();
        Customer existing = customerDAO.searchCustomerById(id);
        if (existing == null) {
            System.out.println("ERROR: Customer not found with ID: " + id);
            return;
        }
        System.out.println("Current: " + existing);
        String username = readNonEmptyString("Enter New Username: ");
        String password = readNonEmptyString("Enter New Password: ");
        String name = readNonEmptyString("Enter New Full Name: ");
        
        String email;
        do {
            email = readNonEmptyString("Enter New Email: ");
            if (!ValidationUtils.isValidEmail(email)) {
                System.out.println("Invalid email format.");
            }
        } while (!ValidationUtils.isValidEmail(email));

        String phone;
        do {
            phone = readNonEmptyString("Enter New Phone: ");
            if (!ValidationUtils.isValidPhone(phone)) {
                System.out.println("Invalid phone format.");
            }
        } while (!ValidationUtils.isValidPhone(phone));

        String passport = readNonEmptyString("Enter New Passport Number: ");

        existing.setUsername(username);
        existing.setPassword(password);
        existing.setName(name);
        existing.setEmail(email);
        existing.setPhone(phone);
        existing.setPassportNumber(passport);

        if (customerDAO.updateCustomer(existing)) {
            System.out.println("SUCCESS: Customer updated successfully!");
        } else {
            System.out.println("ERROR: Failed to update customer.");
        }
    }

    private void deleteCustomer() {
        System.out.println("\n--- Delete Customer ---");
        System.out.print("Enter Customer ID to Delete: ");
        int id = readInt();
        if (customerDAO.deleteCustomer(id)) {
            System.out.println("SUCCESS: Customer deleted successfully!");
        } else {
            System.out.println("ERROR: Customer not found or deletion failed.");
        }
    }

    // Customer Features
    private void searchFlights() {
        System.out.println("\n--- Search Flights ---");
        String dep = readNonEmptyString("Enter Departure City/Airport: ");
        String arr = readNonEmptyString("Enter Arrival City/Airport: ");
        List<Flight> flights = flightDAO.searchFlightsByRoute(dep, arr);
        if (flights.isEmpty()) {
            System.out.println("No flights found for route: " + dep + " -> " + arr);
        } else {
            System.out.println("Available Flights:");
            for (Flight f : flights) {
                System.out.println(f);
            }
        }
    }

    private void bookFlight(Customer customer) {
        System.out.println("\n--- Book Flight ---");
        System.out.print("Enter Flight ID to book: ");
        int flightId = readInt();
        Flight flight = flightDAO.searchFlightById(flightId);
        if (flight == null) {
            System.out.println("ERROR: Flight not found.");
            return;
        }
        if (flight.getAvailableSeats() <= 0) {
            System.out.println("ERROR: No seats available on this flight.");
            return;
        }

        String passName = readNonEmptyString("Enter Passenger Full Name: ");
        System.out.print("Enter Passenger Age: ");
        int age = readInt();
        String gender = readNonEmptyString("Enter Gender (Male/Female/Other): ");
        String seatNo = readNonEmptyString("Preferred Seat Number (e.g. 15B): ");

        // Auto insert current booking date
        String today = DateValidator.today().toString();
        Booking booking = new Booking(customer.getCustomerId(), flightId, today, "CONFIRMED", flight.getPrice());
        int bookingId = bookingDAO.addBooking(booking);

        if (bookingId != -1) {
            Passenger passenger = new Passenger(bookingId, passName, age, gender, seatNo);
            int passengerId = passengerDAO.addPassenger(passenger);
            flightDAO.updateAvailableSeats(flightId, 1);

            String ticketNo = "TKT-" + System.currentTimeMillis() % 100000;
            Ticket ticket = new Ticket(bookingId, passengerId, ticketNo, seatNo, flight.getPrice(), today);
            ticketDAO.addTicket(ticket);

            notificationDAO.addNotification(new Notification(customer.getCustomerId(), "Booking confirmed! Booking ID: " + bookingId + ", Ticket: " + ticketNo, DateValidator.nowTimestamp(), "UNREAD"));

            System.out.println("SUCCESS: Flight booked successfully!");
            System.out.println("Booking ID: " + bookingId + " | Ticket No: " + ticketNo + " | Seat: " + seatNo);
        } else {
            System.out.println("ERROR: Booking failed.");
        }
    }

    private void viewMyBookings(Customer customer) {
        System.out.println("\n--- My Bookings ---");
        List<Booking> list = bookingDAO.getBookingsByCustomerId(customer.getCustomerId());
        if (list.isEmpty()) {
            System.out.println("You have no flight bookings.");
        } else {
            for (Booking b : list) {
                System.out.println(b);
            }
        }
    }

    private void cancelBooking(Customer customer) {
        System.out.println("\n--- Cancel Booking ---");
        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = readInt();
        Booking b = bookingDAO.searchBookingById(bookingId);
        if (b == null || b.getCustomerId() != customer.getCustomerId()) {
            System.out.println("ERROR: Booking not found or does not belong to you.");
            return;
        }

        if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
            System.out.println("INFO: Booking ID " + bookingId + " is ALREADY CANCELLED.");
            return;
        }

        if (bookingDAO.updateBookingStatus(bookingId, "CANCELLED")) {
            // Restore seat count on flight
            flightDAO.updateAvailableSeats(b.getFlightId(), -1);

            // Refund any existing payment
            paymentDAO.updatePaymentStatusByBookingId(bookingId, "REFUNDED");

            notificationDAO.addNotification(new Notification(customer.getCustomerId(), "Booking ID " + bookingId + " has been CANCELLED. Any processed payments have been refunded.", DateValidator.nowTimestamp(), "UNREAD"));
            System.out.println("SUCCESS: Booking ID " + bookingId + " cancelled successfully! Any processed payments have been refunded.");
        } else {
            System.out.println("ERROR: Failed to cancel booking.");
        }
    }

    private void viewTicket(Customer customer) {
        System.out.println("\n--- View Ticket ---");
        System.out.print("Enter Booking ID: ");
        int bookingId = readInt();
        Booking b = bookingDAO.searchBookingById(bookingId);
        if (b == null || b.getCustomerId() != customer.getCustomerId()) {
            System.out.println("ERROR: Booking not found or unauthorized.");
            return;
        }

        if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
            System.out.println("NOTICE: Booking ID " + bookingId + " is CANCELLED. Ticket is no longer valid.");
        }

        List<Ticket> tickets = ticketDAO.getTicketsByBookingId(bookingId);
        if (tickets.isEmpty()) {
            System.out.println("No tickets generated for this booking.");
        } else {
            for (Ticket t : tickets) {
                System.out.println(t + " | Booking Status: " + b.getStatus());
            }
        }
    }

    private void processPayment(Customer customer) {
        System.out.println("\n--- Process Payment ---");
        System.out.print("Enter Booking ID to pay for: ");
        int bookingId = readInt();
        Booking b = bookingDAO.searchBookingById(bookingId);
        if (b == null || b.getCustomerId() != customer.getCustomerId()) {
            System.out.println("ERROR: Invalid Booking ID or booking does not belong to you.");
            return;
        }

        if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
            System.out.println("ERROR: Cannot process payment. Booking ID " + bookingId + " has been CANCELLED.");
            return;
        }

        List<Payment> existingPayments = paymentDAO.getPaymentsByBookingId(bookingId);
        for (Payment p : existingPayments) {
            if ("COMPLETED".equalsIgnoreCase(p.getStatus())) {
                System.out.println("INFO: Payment for Booking ID " + bookingId + " has ALREADY been COMPLETED via " + p.getPaymentMethod() + ".");
                return;
            }
        }

        String method = readNonEmptyString("Select Payment Method (Credit Card/Debit Card/UPI/NetBanking): ");
        String today = DateValidator.today().toString();

        Payment payment = new Payment(bookingId, b.getTotalAmount(), today, method, "COMPLETED");
        if (paymentDAO.addPayment(payment)) {
            System.out.println("SUCCESS: Payment of $" + b.getTotalAmount() + " completed via " + method + "!");
        } else {
            System.out.println("ERROR: Payment failed.");
        }
    }

    private void bookHotel(Customer customer) {
        System.out.println("\n--- Book Hotel ---");
        String city = readNonEmptyString("Enter City to search Hotels: ");
        List<Hotel> hotels = hotelDAO.searchHotelsByCity(city);
        if (hotels.isEmpty()) {
            System.out.println("No hotels available in " + city);
            return;
        }
        for (Hotel h : hotels) {
            System.out.println(h);
        }
        System.out.print("Enter Hotel ID to book: ");
        int hotelId = readInt();
        Hotel hotel = hotelDAO.searchHotelById(hotelId);
        if (hotel == null) {
            System.out.println("ERROR: Hotel not found.");
            return;
        }

        LocalDate checkIn = DateValidator.inputFutureDate(sc, "Check-in Date (yyyy-MM-dd): ");
        LocalDate checkOut = DateValidator.inputCheckOutDate(sc, "Check-out Date (yyyy-MM-dd): ", checkIn);

        System.out.print("Number of Rooms: ");
        int rooms = readInt();
        double total = rooms * hotel.getPricePerNight();

        HotelBooking hb = new HotelBooking(customer.getCustomerId(), hotelId, checkIn.toString(), checkOut.toString(), rooms, total, "CONFIRMED");
        if (hotelBookingDAO.addHotelBooking(hb)) {
            System.out.println("SUCCESS: Hotel booked successfully! Total Amount: $" + total);
        } else {
            System.out.println("ERROR: Hotel booking failed.");
        }
    }

    private void bookTaxi(Customer customer) {
        System.out.println("\n--- Book Taxi ---");
        List<Taxi> taxis = taxiDAO.getAllTaxis();
        if (taxis.isEmpty()) {
            System.out.println("No taxis currently registered.");
            return;
        }
        for (Taxi t : taxis) {
            System.out.println(t);
        }
        System.out.print("Enter Taxi ID to book: ");
        int taxiId = readInt();
        Taxi taxi = taxiDAO.searchTaxiById(taxiId);
        if (taxi == null) {
            System.out.println("ERROR: Taxi not found.");
            return;
        }
        System.out.print("Estimated Distance in KM: ");
        double km = readDouble();
        double cost = km * taxi.getPricePerKm();

        System.out.println("SUCCESS: Taxi booked with Driver " + taxi.getDriverName() + " (" + taxi.getPhoneNumber() + "). Estimated Cost: $" + cost);
    }

    private void viewNotifications(Customer customer) {
        System.out.println("\n--- My Notifications ---");
        List<Notification> list = notificationDAO.getNotificationsByCustomerId(customer.getCustomerId());
        if (list.isEmpty()) {
            System.out.println("No notifications.");
        } else {
            for (Notification n : list) {
                System.out.println(n);
            }
        }
    }

    private void giveFeedback(Customer customer) {
        System.out.println("\n--- Give Feedback ---");
        int rating;
        do {
            System.out.print("Rating (1-5 Stars): ");
            rating = readInt();
            if (!ValidationUtils.isValidRating(rating)) {
                System.out.println("Rating must be between 1 and 5.");
            }
        } while (!ValidationUtils.isValidRating(rating));

        String comments = readNonEmptyString("Comments / Experience: ");
        String today = DateValidator.today().toString();

        Feedback fb = new Feedback(customer.getCustomerId(), rating, comments, today);
        if (feedbackDAO.addFeedback(fb)) {
            System.out.println("SUCCESS: Thank you for your feedback!");
        } else {
            System.out.println("ERROR: Failed to submit feedback.");
        }
    }

    private int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Invalid input. Enter a valid integer: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    private double readDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Invalid input. Enter a valid number: ");
            sc.next();
        }
        double val = sc.nextDouble();
        sc.nextLine();
        return val;
    }

    private String readNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (!ValidationUtils.isNotEmpty(input)) {
                System.out.println("Input cannot be blank. Please try again.");
            }
        } while (!ValidationUtils.isNotEmpty(input));
        return input;
    }
}
