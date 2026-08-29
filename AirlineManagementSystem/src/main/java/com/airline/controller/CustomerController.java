package com.airline.controller;

import dao.BookingDAO;
import dao.CustomerDAO;
import dao.FlightDAO;
import dao.HotelDAO;
import model.Booking;
import model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerDAO customerDAO;
    private final FlightDAO flightDAO;
    private final BookingDAO bookingDAO;
    private final HotelDAO hotelDAO;

    public CustomerController(CustomerDAO customerDAO, FlightDAO flightDAO, BookingDAO bookingDAO, HotelDAO hotelDAO) {
        this.customerDAO = customerDAO;
        this.flightDAO = flightDAO;
        this.bookingDAO = bookingDAO;
        this.hotelDAO = hotelDAO;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        model.addAttribute("customer", customer);
        model.addAttribute("flights", flightDAO.getAllFlights());
        model.addAttribute("bookings", bookingDAO.getBookingsByCustomerId(customer.getCustomerId()));
        model.addAttribute("hotels", hotelDAO.getAllHotels());
        return "customer-dashboard";
    }

    @GetMapping("/book/{flightId}")
    public String bookFlight(@PathVariable int flightId, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/login";
        }

        model.addAttribute("customer", customer);
        model.addAttribute("flight", flightDAO.searchFlightById(flightId));
        model.addAttribute("booking", new Booking());
        return "customer-book";
    }

    @PostMapping("/book")
    public String createBooking(@ModelAttribute Booking booking, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer != null) {
            booking.setCustomerId(customer.getCustomerId());
            booking.setStatus("CONFIRMED");
            bookingDAO.addBooking(booking);
        }
        return "redirect:/customer/dashboard";
    }
}
