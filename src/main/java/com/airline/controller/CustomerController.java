package com.airline.controller;

import dao.*;
import model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerDAO customerDAO;
    private final FlightDAO flightDAO;
    private final BookingDAO bookingDAO;
    private final PassengerDAO passengerDAO;
    private final TicketDAO ticketDAO;
    private final PaymentDAO paymentDAO;
    private final HotelDAO hotelDAO;
    private final HotelBookingDAO hotelBookingDAO;
    private final TaxiDAO taxiDAO;
    private final NotificationDAO notificationDAO;
    private final FeedbackDAO feedbackDAO;

    public CustomerController(CustomerDAO customerDAO, FlightDAO flightDAO, BookingDAO bookingDAO,
                              PassengerDAO passengerDAO, TicketDAO ticketDAO, PaymentDAO paymentDAO,
                              HotelDAO hotelDAO, HotelBookingDAO hotelBookingDAO, TaxiDAO taxiDAO,
                              NotificationDAO notificationDAO, FeedbackDAO feedbackDAO) {
        this.customerDAO = customerDAO;
        this.flightDAO = flightDAO;
        this.bookingDAO = bookingDAO;
        this.passengerDAO = passengerDAO;
        this.ticketDAO = ticketDAO;
        this.paymentDAO = paymentDAO;
        this.hotelDAO = hotelDAO;
        this.hotelBookingDAO = hotelBookingDAO;
        this.taxiDAO = taxiDAO;
        this.notificationDAO = notificationDAO;
        this.feedbackDAO = feedbackDAO;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String activeTab,
                            @RequestParam(required = false) String success,
                            @RequestParam(required = false) String error,
                            HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        // Refresh customer data from DB if available
        Customer freshCustomer = customerDAO.searchCustomerById(customer.getCustomerId());
        if (freshCustomer != null) {
            customer = freshCustomer;
            session.setAttribute("customer", customer);
        }

        model.addAttribute("customer", customer);
        model.addAttribute("flights", flightDAO.getAllFlights());

        List<Booking> bookings = bookingDAO.getBookingsByCustomerId(customer.getCustomerId());
        model.addAttribute("bookings", bookings);

        List<Ticket> tickets = new ArrayList<>();
        for (Booking b : bookings) {
            tickets.addAll(ticketDAO.getTicketsByBookingId(b.getBookingId()));
        }
        model.addAttribute("tickets", tickets);

        model.addAttribute("hotels", hotelDAO.getAllHotels());
        model.addAttribute("hotelBookings", hotelBookingDAO.getHotelBookingsByCustomerId(customer.getCustomerId()));
        model.addAttribute("taxis", taxiDAO.getAllTaxis());
        model.addAttribute("notifications", notificationDAO.getNotificationsByCustomerId(customer.getCustomerId()));
        model.addAttribute("feedbacks", feedbackDAO.getFeedbackByCustomerId(customer.getCustomerId()));

        model.addAttribute("activeTab", activeTab != null ? activeTab : "flights");
        model.addAttribute("success", success);
        model.addAttribute("error", error);

        return "customer-dashboard";
    }

    @GetMapping("/book/{flightId}")
    public String bookFlight(@PathVariable int flightId, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        Flight flight = flightDAO.searchFlightById(flightId);
        if (flight == null) {
            return "redirect:/customer/dashboard?error=Flight+not+found.";
        }

        model.addAttribute("customer", customer);
        model.addAttribute("flight", flight);
        return "customer-book";
    }

    @PostMapping("/book")
    public String createBooking(@RequestParam int flightId,
                                @RequestParam String passengerName,
                                @RequestParam int passengerAge,
                                @RequestParam String passengerGender,
                                @RequestParam String seatNumber,
                                @RequestParam(defaultValue = "Indian") String nationality,
                                @RequestParam(defaultValue = "Standard") String mealPreference,
                                @RequestParam(defaultValue = "Economy") String cabinClass,
                                @RequestParam String paymentMethod,
                                HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        Flight flight = flightDAO.searchFlightById(flightId);
        if (flight == null || flight.getAvailableSeats() <= 0) {
            return "redirect:/customer/dashboard?error=Selected+flight+is+fully+booked.";
        }

        double finalPrice = flight.getPrice();
        if ("Business".equalsIgnoreCase(cabinClass)) {
            finalPrice *= 2.0;
        } else if ("Premium Economy".equalsIgnoreCase(cabinClass)) {
            finalPrice *= 1.4;
        } else if ("First".equalsIgnoreCase(cabinClass)) {
            finalPrice *= 3.0;
        }

        String today = LocalDate.now().toString();
        Booking booking = new Booking(0, customer.getCustomerId(), flightId, today, "CONFIRMED", finalPrice, null, cabinClass);
        int bookingId = bookingDAO.addBooking(booking);

        if (bookingId > 0) {
            Booking freshBooking = bookingDAO.searchBookingById(bookingId);
            String pnr = (freshBooking != null && freshBooking.getPnrCode() != null) ? freshBooking.getPnrCode() : "SKY" + (100 + new Random().nextInt(899));

            Passenger passenger = new Passenger(0, bookingId, passengerName, passengerAge, passengerGender, seatNumber, nationality, mealPreference);
            int passengerId = passengerDAO.addPassenger(passenger);

            if (passengerId <= 0) {
                passengerId = 1;
            }

            String ticketNum = "TKT-" + (10000 + new Random().nextInt(90000));
            Ticket ticket = new Ticket(bookingId, passengerId, ticketNum, seatNumber, finalPrice, today);
            ticketDAO.addTicket(ticket);

            Payment payment = new Payment(bookingId, finalPrice, today, paymentMethod, "COMPLETED");
            paymentDAO.addPayment(payment);

            flightDAO.updateAvailableSeats(flightId, 1);

            String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Notification note = new Notification(customer.getCustomerId(),
                    "Flight " + flight.getFlightNumber() + " confirmed! PNR: " + pnr + " • Ticket: " + ticketNum, timeNow, "UNREAD");
            notificationDAO.addNotification(note);

            return "redirect:/customer/ticket/number/" + ticketNum + "?success=Flight+booked+successfully!+PNR:+"+pnr;
        }

        return "redirect:/customer/dashboard?error=Booking+failed.+Please+try+again.";
    }

    @GetMapping("/ticket/{ticketId}")
    public String viewTicket(@PathVariable int ticketId, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        Ticket ticket = ticketDAO.searchTicketById(ticketId);
        if (ticket == null) {
            return "redirect:/customer/dashboard?error=Ticket+not+found.";
        }

        Booking booking = bookingDAO.searchBookingById(ticket.getBookingId());
        Flight flight = (booking != null) ? flightDAO.searchFlightById(booking.getFlightId()) : null;
        Passenger passenger = passengerDAO.searchPassengerById(ticket.getPassengerId());

        model.addAttribute("ticket", ticket);
        model.addAttribute("booking", booking);
        model.addAttribute("flight", flight);
        model.addAttribute("passenger", passenger != null ? passenger : new Passenger(0, customer.getName(), 30, "N/A", ticket.getSeatNumber()));
        model.addAttribute("customer", customer);

        return "ticket-view";
    }

    @GetMapping("/ticket/number/{ticketNumber}")
    public String viewTicketByNumber(@PathVariable String ticketNumber, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        Ticket ticket = ticketDAO.searchTicketByNumber(ticketNumber);
        if (ticket == null) {
            return "redirect:/customer/dashboard?error=Ticket+not+found.";
        }

        return viewTicket(ticket.getTicketId(), session, model);
    }

    @PostMapping("/booking/cancel/{bookingId}")
    public String cancelBooking(@PathVariable int bookingId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        Booking booking = bookingDAO.searchBookingById(bookingId);
        if (booking != null && booking.getCustomerId() == customer.getCustomerId()) {
            bookingDAO.updateBookingStatus(bookingId, "CANCELLED");
            
            String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            notificationDAO.addNotification(new Notification(customer.getCustomerId(),
                    "Booking #" + bookingId + " has been cancelled.", timeNow, "UNREAD"));
            return "redirect:/customer/dashboard?activeTab=bookings&success=Booking+cancelled+successfully.";
        }

        return "redirect:/customer/dashboard?error=Unable+to+cancel+booking.";
    }

    @PostMapping("/hotel/book")
    public String bookHotel(@RequestParam int hotelId,
                            @RequestParam String checkInDate,
                            @RequestParam String checkOutDate,
                            @RequestParam int numRooms,
                            HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        Hotel hotel = hotelDAO.searchHotelById(hotelId);
        if (hotel != null) {
            double totalPrice = hotel.getPricePerNight() * numRooms;
            HotelBooking hb = new HotelBooking(customer.getCustomerId(), hotelId, checkInDate, checkOutDate, numRooms, totalPrice, "CONFIRMED");
            hotelBookingDAO.addHotelBooking(hb);

            String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            notificationDAO.addNotification(new Notification(customer.getCustomerId(),
                    "Hotel reservation confirmed at " + hotel.getHotelName() + " (" + numRooms + " room/s).", timeNow, "UNREAD"));

            return "redirect:/customer/dashboard?activeTab=hotels&success=Hotel+booked+successfully!";
        }

        return "redirect:/customer/dashboard?activeTab=hotels&error=Hotel+booking+failed.";
    }

    @PostMapping("/taxi/book")
    public String bookTaxi(@RequestParam int taxiId, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        Taxi taxi = taxiDAO.searchTaxiById(taxiId);
        if (taxi != null) {
            taxiDAO.updateTaxiStatus(taxiId, "BOOKED");
            String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            notificationDAO.addNotification(new Notification(customer.getCustomerId(),
                    "Taxi transfer booked with driver " + taxi.getDriverName() + " (" + taxi.getVehicleNumber() + ").", timeNow, "UNREAD"));

            return "redirect:/customer/dashboard?activeTab=taxis&success=Taxi+transfer+booked+successfully!";
        }

        return "redirect:/customer/dashboard?activeTab=taxis&error=Taxi+booking+failed.";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam int rating,
                                 @RequestParam String comments,
                                 HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        String today = LocalDate.now().toString();
        Feedback fb = new Feedback(customer.getCustomerId(), rating, comments, today);
        feedbackDAO.addFeedback(fb);

        return "redirect:/customer/dashboard?activeTab=feedback&success=Thank+you+for+your+feedback!";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam String passportNumber,
                                @RequestParam(required = false) String password,
                                HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setPassportNumber(passportNumber);
        if (password != null && !password.trim().isEmpty()) {
            customer.setPassword(password);
        }

        customerDAO.updateCustomer(customer);
        session.setAttribute("customer", customer);
        session.setAttribute("username", customer.getName());

        return "redirect:/customer/dashboard?activeTab=profile&success=Profile+updated+successfully!";
    }
}
