package com.airline.controller;

import dao.*;
import model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminDAO adminDAO;
    private final CustomerDAO customerDAO;
    private final FlightDAO flightDAO;
    private final AirportDAO airportDAO;
    private final BookingDAO bookingDAO;
    private final TicketDAO ticketDAO;
    private final HotelDAO hotelDAO;
    private final TaxiDAO taxiDAO;
    private final NotificationDAO notificationDAO;
    private final FeedbackDAO feedbackDAO;

    public AdminController(AdminDAO adminDAO, CustomerDAO customerDAO, FlightDAO flightDAO,
                           AirportDAO airportDAO, BookingDAO bookingDAO, TicketDAO ticketDAO,
                           HotelDAO hotelDAO, TaxiDAO taxiDAO, NotificationDAO notificationDAO,
                           FeedbackDAO feedbackDAO) {
        this.adminDAO = adminDAO;
        this.customerDAO = customerDAO;
        this.flightDAO = flightDAO;
        this.airportDAO = airportDAO;
        this.bookingDAO = bookingDAO;
        this.ticketDAO = ticketDAO;
        this.hotelDAO = hotelDAO;
        this.taxiDAO = taxiDAO;
        this.notificationDAO = notificationDAO;
        this.feedbackDAO = feedbackDAO;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Customer> customers = customerDAO.getAllCustomers();
        List<Flight> flights = flightDAO.getAllFlights();
        List<Booking> bookings = bookingDAO.getAllBookings();
        List<Hotel> hotels = hotelDAO.getAllHotels();
        List<Taxi> taxis = taxiDAO.getAllTaxis();
        List<Feedback> feedbacks = feedbackDAO.getAllFeedback();

        double totalRevenue = bookings.stream()
                .filter(b -> !"CANCELLED".equalsIgnoreCase(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        model.addAttribute("customers", customers);
        model.addAttribute("flights", flights);
        model.addAttribute("bookings", bookings);
        model.addAttribute("hotels", hotels);
        model.addAttribute("taxis", taxis);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin-dashboard";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerDAO.getAllCustomers());
        return "admin-customers";
    }

    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable int id) {
        customerDAO.deleteCustomer(id);
        return "redirect:/admin/customers?success=Customer+removed.";
    }

    @GetMapping("/flights")
    public String flights(Model model) {
        model.addAttribute("flights", flightDAO.getAllFlights());
        model.addAttribute("flight", new Flight());
        model.addAttribute("airports", airportDAO.getAllAirports());
        return "admin-flights";
    }

    @PostMapping("/flights")
    public String saveFlight(@ModelAttribute Flight flight) {
        if (flight.getFlightId() > 0) {
            flightDAO.updateFlight(flight);
        } else {
            flightDAO.addFlight(flight);
        }
        return "redirect:/admin/flights?success=Flight+saved+successfully.";
    }

    @PostMapping("/flights/delete/{id}")
    public String deleteFlight(@PathVariable int id) {
        flightDAO.deleteFlight(id);
        return "redirect:/admin/flights?success=Flight+deleted.";
    }

    @GetMapping("/airports")
    public String airports(Model model) {
        model.addAttribute("airports", airportDAO.getAllAirports());
        model.addAttribute("airport", new Airport());
        return "admin-airports";
    }

    @PostMapping("/airports")
    public String saveAirport(@ModelAttribute Airport airport) {
        if (airport.getAirportId() > 0) {
            airportDAO.updateAirport(airport);
        } else {
            airportDAO.addAirport(airport);
        }
        return "redirect:/admin/airports?success=Airport+saved+successfully.";
    }

    @PostMapping("/airports/delete/{id}")
    public String deleteAirport(@PathVariable int id) {
        airportDAO.deleteAirport(id);
        return "redirect:/admin/airports?success=Airport+deleted.";
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", bookingDAO.getAllBookings());
        model.addAttribute("flights", flightDAO.getAllFlights());
        model.addAttribute("customers", customerDAO.getAllCustomers());
        return "admin-bookings";
    }

    @PostMapping("/bookings/status")
    public String updateBookingStatus(@RequestParam int bookingId, @RequestParam String status) {
        bookingDAO.updateBookingStatus(bookingId, status);
        return "redirect:/admin/bookings?success=Booking+status+updated.";
    }

    @GetMapping("/hotels")
    public String hotels(Model model) {
        model.addAttribute("hotels", hotelDAO.getAllHotels());
        model.addAttribute("hotel", new Hotel());
        return "admin-hotels";
    }

    @PostMapping("/hotels")
    public String saveHotel(@ModelAttribute Hotel hotel) {
        if (hotel.getHotelId() > 0) {
            hotelDAO.updateHotel(hotel);
        } else {
            hotelDAO.addHotel(hotel);
        }
        return "redirect:/admin/hotels?success=Hotel+saved.";
    }

    @PostMapping("/hotels/delete/{id}")
    public String deleteHotel(@PathVariable int id) {
        hotelDAO.deleteHotel(id);
        return "redirect:/admin/hotels?success=Hotel+deleted.";
    }

    @GetMapping("/taxis")
    public String taxis(Model model) {
        model.addAttribute("taxis", taxiDAO.getAllTaxis());
        model.addAttribute("taxi", new Taxi());
        return "admin-taxis";
    }

    @PostMapping("/taxis")
    public String saveTaxi(@ModelAttribute Taxi taxi) {
        if (taxi.getTaxiId() > 0) {
            taxiDAO.updateTaxi(taxi);
        } else {
            taxiDAO.addTaxi(taxi);
        }
        return "redirect:/admin/taxis?success=Taxi+saved.";
    }

    @PostMapping("/taxis/status")
    public String updateTaxiStatus(@RequestParam int taxiId, @RequestParam String status) {
        taxiDAO.updateTaxiStatus(taxiId, status);
        return "redirect:/admin/taxis?success=Taxi+status+updated.";
    }

    @PostMapping("/taxis/delete/{id}")
    public String deleteTaxi(@PathVariable int id) {
        taxiDAO.deleteTaxi(id);
        return "redirect:/admin/taxis?success=Taxi+deleted.";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("notifications", notificationDAO.getAllNotifications());
        model.addAttribute("customers", customerDAO.getAllCustomers());
        return "admin-notifications";
    }

    @PostMapping("/notifications")
    public String sendNotification(@RequestParam int customerId, @RequestParam String message) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (customerId == 0) {
            // Broadcast to all customers
            for (Customer c : customerDAO.getAllCustomers()) {
                notificationDAO.addNotification(new Notification(c.getCustomerId(), message, now, "UNREAD"));
            }
        } else {
            notificationDAO.addNotification(new Notification(customerId, message, now, "UNREAD"));
        }
        return "redirect:/admin/notifications?success=Notification+sent.";
    }

    @GetMapping("/feedbacks")
    public String feedbacks(Model model) {
        model.addAttribute("feedbacks", feedbackDAO.getAllFeedback());
        model.addAttribute("customers", customerDAO.getAllCustomers());
        return "admin-feedbacks";
    }
}
